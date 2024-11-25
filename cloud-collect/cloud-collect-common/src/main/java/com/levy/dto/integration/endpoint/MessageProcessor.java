package com.levy.dto.integration.endpoint;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Optional;

/**
 * 消息处理器
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
public interface MessageProcessor<P> extends Concurrent {

    String from();

    default String to() {
        return null;
    }

    default Object process(Message<P> message) throws Exception {
        return process(message.getPayload(), message.getHeaders());
    }

    default Object process(P payload, MessageHeaders headers) throws Exception {
        return Optional.ofNullable(process(payload)).orElse(process(headers));
    }

    default Object process(P payload) throws Exception {
        return process();
    }

    default Object process() throws Exception {
        return null;
    }

    default Object process(MessageHeaders headers) throws Exception {
        return null;
    }

}
