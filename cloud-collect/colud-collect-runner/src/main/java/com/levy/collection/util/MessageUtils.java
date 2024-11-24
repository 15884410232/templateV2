package com.levy.collection.util;

import com.dtsw.collection.enumeration.FlowParameter;
import org.springframework.messaging.MessageHeaders;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
public class MessageUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getParameter(MessageHeaders headers, FlowParameter parameter) {
        return (T) headers.get(parameter.getName(), parameter.getType().getType());
    }

    public static Object getParameter(MessageHeaders headers, String taskId) {
        return headers.get(taskId);
    }

    public static <T> T getParameter(MessageHeaders headers, String taskId, Class<T> type) {
        return headers.get(taskId, type);
    }
}
