package com.dtsw.integration.support;

import com.dtsw.integration.annotation.BaseIntegrationConfig;
import com.dtsw.integration.annotation.IntegrationConfigurer;
import com.dtsw.integration.annotation.MessageInterceptor;
import com.dtsw.integration.endpoint.MessageProcessor;
import com.dtsw.integration.endpoint.MessageRouter;
import com.dtsw.integration.endpoint.MessageSplitter;
import com.dtsw.integration.handler.InterceptorMessageHandler;
import com.dtsw.integration.handler.ProcessorMessageHandler;
import com.dtsw.integration.handler.RouterMessageHandler;
import com.dtsw.integration.handler.SplitterMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@AutoConfigureAfter(MessageChannelRegistrar.class)
@RequiredArgsConstructor
public class MessageHandlerRegistrar implements InitializingBean, ApplicationContextAware {

    private final List<MessageInterceptor> interceptors = new ArrayList<>();
    @Setter(onMethod = @__({@Override}))
    private ApplicationContext applicationContext;
    @Setter
    private ConversionService conversionService;

    @Override
    public void afterPropertiesSet() {
        // 配置拦截器
        Map<String, IntegrationConfigurer> configurerMap = applicationContext.getBeansOfType(IntegrationConfigurer.class);
        if (!configurerMap.isEmpty()) {
            MessageInterceptorRegistry interceptorRegistry = new MessageInterceptorRegistry();
            configurerMap.forEach((key, value) -> value.addInterceptors(interceptorRegistry));
            interceptors.addAll(interceptorRegistry.getInterceptors());
        }

        registerPollingConsumer();
    }

    private void registerPollingConsumer() {
        var processors = this.applicationContext.getBeansOfType(MessageProcessor.class);
        processors.forEach((name, processor) -> {
            ProcessorMessageHandler handler = new ProcessorMessageHandler(conversionService, processor);
            registerPollingConsumer(name, handler, processor.from(), processor.concurrency());
        });

        var routers = this.applicationContext.getBeansOfType(MessageRouter.class);
        routers.forEach((name, router) -> {
            RouterMessageHandler handler = new RouterMessageHandler(conversionService, router);
            registerPollingConsumer(name, handler, router.from(), router.concurrency());
        });

        var splitters = this.applicationContext.getBeansOfType(MessageSplitter.class);
        splitters.forEach((name, splitter) -> {
            SplitterMessageHandler handler = new SplitterMessageHandler(conversionService, splitter);
            registerPollingConsumer(name, handler, splitter.from(), splitter.concurrency());
        });
    }

    private void registerPollingConsumer(String beanName, MessageHandler handler, String fromChannel, Integer concurrency) {
        if (applicationContext instanceof ConfigurableApplicationContext context) {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getBeanFactory();
            PollableChannel channel = applicationContext.getBean(fromChannel, PollableChannel.class);
            if (handler instanceof ApplicationContextAware) {
                ((ApplicationContextAware) handler).setApplicationContext(applicationContext);
            }
            PollingConsumer consumer = new PollingConsumer(channel, new InterceptorMessageHandler(interceptors, handler));
            BaseIntegrationConfig baseIntegrationConfig = this.applicationContext.getBean(BaseIntegrationConfig.class);
//            Executor executor = Executors.newFixedThreadPool(Optional.ofNullable(concurrency).orElse(baseIntegrationConfig.getConcurrency()));
            //此处需要使用有界队列或者阻塞操作，因为轮询消息的线程池会根据轮询调度，
            // 不停的向线程池中提交轮询任务，如果消息消费过程很慢，会导致轮询任务队列积压，
            // 如果使用无界队列，就会导致内存占用不停的增加直到OOM
            // 同事拒绝策略也不能使用阻塞模式，因为一旦提交任务的调度线程阻塞，会导致轮询任务无法继续执行，导致其他的消费者也阻塞，无法执行消费任务，所以直接丢弃掉任务即可
            ThreadPoolExecutor executor = new ThreadPoolExecutor(Optional.ofNullable(concurrency).orElse(baseIntegrationConfig.getConcurrency()),
                    Optional.ofNullable(concurrency).orElse(baseIntegrationConfig.getConcurrency()),
                    0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),new ThreadPoolExecutor.DiscardPolicy());
            consumer.setTaskExecutor(executor);
            consumer.setBeanFactory(applicationContext);
            factory.registerSingleton(beanName + "PollingConsumer", consumer);
        }
    }
}
