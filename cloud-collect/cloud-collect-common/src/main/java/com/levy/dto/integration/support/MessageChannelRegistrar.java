package com.levy.dto.integration.support;

import com.levy.dto.integration.annotation.IntegrationConfigurer;
import com.levy.dto.integration.endpoint.MessageProcessor;
import com.levy.dto.integration.endpoint.MessageRouter;
import com.levy.dto.integration.endpoint.MessageSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.redis.store.RedisChannelMessageStore;
import org.springframework.integration.store.MessageGroupQueue;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class MessageChannelRegistrar implements InitializingBean, ApplicationContextAware {

    private final RedisChannelMessageStore messageStore;
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() {
        Set<String> channels = new HashSet<>();

        // 配置拦截器
        Map<String, IntegrationConfigurer> configurerMap = applicationContext.getBeansOfType(IntegrationConfigurer.class);
        if (!configurerMap.isEmpty()) {
            MessageChannelRegistry messageChannelRegistry = new MessageChannelRegistry();
            configurerMap.forEach((key, value) -> value.addMessageChannelRegistrar(messageChannelRegistry));
            channels.addAll(messageChannelRegistry.getChannels());
        }
        var processors = this.applicationContext.getBeansOfType(MessageProcessor.class);
        processors.values().stream().flatMap(bean -> Stream.of(bean.from(), bean.to())).filter(Objects::nonNull).forEach(channels::add);

        var routers = this.applicationContext.getBeansOfType(MessageRouter.class);
        routers.values().stream().map(MessageRouter::from).filter(Objects::nonNull).forEach(channels::add);
        routers.values().stream().map(r -> (Collection<String>) r.to()).flatMap(Collection::stream).filter(Objects::nonNull).forEach(channels::add);

        var splitters = this.applicationContext.getBeansOfType(MessageSplitter.class);
        splitters.values().stream().flatMap(bean -> Stream.of(bean.from(), bean.to())).filter(Objects::nonNull).forEach(channels::add);

        registerChannel(channels);
    }

    private void registerChannel(Set<String> channels) {
        if (applicationContext instanceof ConfigurableApplicationContext context) {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getBeanFactory();
            channels.stream()
                    .filter(Objects::nonNull)
                    .filter(name -> !name.isEmpty())
                    .forEach(name -> {
                        MessageGroupQueue queue = new MessageGroupQueue(messageStore, name);
                        QueueChannel channel = new QueueChannel(queue);
                        factory.registerSingleton(name, channel);
                    });
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
