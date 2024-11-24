package com.dtsw.integration.endpoint;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Collection;
import java.util.Optional;

/**
 * 消息路由器
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
public interface MessageRouter<P> extends Concurrent {

    String from();

    Collection<String> to();

    default String routing(Message<P> message) throws Exception {
        return routing(message.getPayload(), message.getHeaders());
    }

    default String routing(P payload, MessageHeaders headers) throws Exception {
        return Optional.ofNullable(routing(payload)).orElse(routing(headers));
    }

    default String routing(P payload) throws Exception {
        return routing();
    }

    default String routing() throws Exception {
        return null;
    }

    default String routing(MessageHeaders headers) throws Exception {
        return null;
    }

}
