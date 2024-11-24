package com.dtsw.integration.handler;

import com.dtsw.integration.annotation.MessageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.List;

/**
 * 消息处理器执行器
 *
 * @author Wangwang Tang
 * @since 2024-11-04
 */
@Slf4j
public class InterceptorMessageHandler implements MessageHandler {

    private final MessageHandlerExecutionChain chain;

    public InterceptorMessageHandler(MessageHandlerExecutionChain chain) {
        this.chain = chain;
    }

    public InterceptorMessageHandler(List<MessageInterceptor> interceptors, MessageHandler handler) {
        this.chain = new MessageHandlerExecutionChain(handler, interceptors);
    }

    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        Exception handlerException = null;
        try {
            // call interceptors preHandler method
            if (!chain.applyPreHandle(message)) {
                return;
            }

            // Actually invoke the handler.
            chain.getHandler().handleMessage(message);

            // call interceptors postHandler method
            chain.applyPostHandle(message);
        } catch (Exception ex) {
            log.error("Handler message error: {}", ex.getMessage());
            handlerException = ex;
        } catch (Throwable err) {
            log.error("Handler message failed: {}", err.getMessage());
            handlerException = new MessagingException("Handler message failed: " + err, err);
        }

        // call interceptors afterCompletion method
        chain.triggerAfterCompletion(message, handlerException);
    }
}
