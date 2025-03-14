package com.dtsw.integration.handler;

import com.dtsw.integration.endpoint.MessageRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.PollableChannel;
import org.springframework.util.Assert;

/**
 * 执行 MessageRouter 的处理方法 routing 的实现
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Slf4j
public class RouterMessageHandler extends AbstractMessageHandler implements MessageHandler {

    private final MessageRouter<?> router;

    public RouterMessageHandler(MessageRouter<?> router) {
        this(null, router);
    }

    public RouterMessageHandler(ConversionService conversionService, MessageRouter<?> router) {
        super(conversionService);
        Assert.notNull(router, "router must not be null");
        Assert.notNull(router.to(), "to value of router must not be null");
        this.router = router;
    }

    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        try {
            // TODO 添加异步处理，监控处理状态，方便设置超时和中止执行
            String toChannel = router.routing(convertMessage(message, router.getClass()));

            if (toChannel == null) {
                log.warn("The message routing result is null, and the message is about to be dropped. message: {}", message);
                return;
            }
            if (!router.to().contains(toChannel)) {
                log.error("The message routing result [{}] is undefined. message: {}", toChannel, message);
                throw new MessagingException(message, "The message routing result [" + toChannel + "] is undefined");
            }

            PollableChannel channel = applicationContext.getBean(toChannel, PollableChannel.class);
            sendMessage(channel, message.getPayload(), message.getHeaders());

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("MessageRouterHandler.handleMessage threw exception", e);
            throw new MessagingException(e.getMessage(), e);
        }
    }
}
