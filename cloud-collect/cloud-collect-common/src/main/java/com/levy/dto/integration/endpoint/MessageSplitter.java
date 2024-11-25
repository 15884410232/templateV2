package com.levy.dto.integration.endpoint;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Collection;

/**
 * 消息分割器
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
public interface MessageSplitter<P> extends Concurrent {

    String from();

    String to();

    default Collection<?> split(Message<P> message) throws Exception {
        return split(message.getPayload(), message.getHeaders());
    }

    default Collection<?> split(P payload, MessageHeaders headers) throws Exception {
        Collection<?> payloadResult = split(payload);
        if (payloadResult != null) {
            return payloadResult;
        } else {
            return split(headers);
        }
    }

    default Collection<?> split(P payload) throws Exception {
        return split();
    }

    default Collection<?> split() throws Exception {
        return null;
    }

    default Collection<?> split(MessageHeaders headers) throws Exception {
        return null;
    }
}
