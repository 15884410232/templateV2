package com.levy.dto.integration.handler;

import com.levy.dto.integration.endpoint.MessageSplitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.PollableChannel;
import org.springframework.util.Assert;

/**
 * 执行 MessageSplitter 的处理方法 split 的实现
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Slf4j
public class SplitterMessageHandler extends AbstractMessageHandler implements MessageHandler {

    private final MessageSplitter<?> splitter;

    public SplitterMessageHandler(MessageSplitter<?> splitter) {
        this(null, splitter);
    }

    public SplitterMessageHandler(ConversionService conversionService, MessageSplitter<?> splitter) {
        super(conversionService);
        Assert.notNull(splitter, "splitter must not be null");
        this.splitter = splitter;
    }

    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        try {
            // TODO 添加异步处理，监控处理状态，方便设置超时和中止执行
            Iterable<?> result = splitter.split(convertMessage(message, splitter.getClass()));
            if (result == null || splitter.to() == null) {
                return;
            }

            PollableChannel channel = applicationContext.getBean(splitter.to(), PollableChannel.class);

            for (Object element : result) {
                sendMessage(channel, element, message.getHeaders());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("MessageSplitterHandler.handleMessage threw exception", e);
            throw new MessagingException(e.getMessage(), e);
        }
    }

}
