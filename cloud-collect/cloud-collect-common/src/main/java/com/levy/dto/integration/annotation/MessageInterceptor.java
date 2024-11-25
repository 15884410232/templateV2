package com.levy.dto.integration.annotation;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.web.method.HandlerMethod;

/**
 * Workflow interface that allows for customized handler execution chains.
 * Applications can register any number of existing or custom interceptors
 * for certain groups of handlers, to add common preprocessing behavior
 * without needing to modify each handler implementation.
 *
 * @author Wangwang Tang
 * @since 2024-11-04
 */
public interface MessageInterceptor {

    /**
     * Interception point before the execution of a handler.
     * <p>The default implementation returns {@code true}.
     *
     * @param message current message
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, return false.
     * @throws Exception in case of errors
     */
    default boolean preHandle(Message<?> message, Object handler) throws Exception {
        return true;
    }

    /**
     * Interception point after successful execution of a handler.
     * <p>The default implementation is empty.
     *
     * @param message     current message
     * @param handler     the handler (or {@link HandlerMethod}) that started asynchronous
     *                    execution, for type and/or instance examination
     * @throws Exception in case of errors
     */
    default void postHandle(Message<?> message, Object handler) throws Exception {
    }

    /**
     * Callback after completion of request processing.
     * Will be called on any outcome of handler execution, thus allows for proper resource cleanup.
     * <p>Note: Will only be called if this interceptor's {@code preHandle}
     * method has successfully completed and returned {@code true}!
     * <p>As with the {@code postHandle} method, the method will be invoked on each
     * interceptor in the chain in reverse order, so the first interceptor will be
     * the last to be invoked.
     * <p>The default implementation is empty.
     *
     * @param message current message
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     *                execution, for type and/or instance examination
     * @param ex      any exception thrown on handler execution, if any; this does not
     *                include exceptions that have been handled through an exception resolver
     * @throws Exception in case of errors
     */
    default void afterCompletion(Message<?> message, Object handler, @Nullable Exception ex) throws Exception {
    }

}
