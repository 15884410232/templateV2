package com.levy.dto.integration.handler;

import com.levy.dto.integration.endpoint.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.PollableChannel;
import org.springframework.util.Assert;

/**
 * 执行 MessageProcessor 的处理方法 process 的实现
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Slf4j
public class ProcessorMessageHandler extends AbstractMessageHandler implements MessageHandler {

    private final MessageProcessor<?> processor;

    public ProcessorMessageHandler(MessageProcessor<?> processor) {
        this(null, processor);
    }

    public ProcessorMessageHandler(ConversionService conversionService, MessageProcessor<?> processor) {
        super(conversionService);
        Assert.notNull(processor, "processor must not be null");
        this.processor = processor;
    }

    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        try {
            // TODO 添加异步处理，监控处理状态，方便设置超时和中止执行
            Object result = processor.process(convertMessage(message, processor.getClass()));
            if (result != null && processor.to() != null) {
                PollableChannel channel = applicationContext.getBean(processor.to(), PollableChannel.class);

                sendMessage(channel, result, message.getHeaders());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("MessageProcessorHandler.handleMessage threw exception", e);
            throw new MessagingException(e.getMessage(), e);
        }
    }
}
