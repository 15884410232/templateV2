package com.levy.dto.integration.handler;

import com.levy.dto.integration.annotation.MessageInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Wangwang Tang
 * @since 2024-11-04
 */
@Slf4j
public class MessageHandlerExecutionChain {

    /**
     * -- GETTER --
     * Return the handler object to execute.
     */
    @Getter
    private final MessageHandler handler;

    private final List<MessageInterceptor> interceptorList = new ArrayList<>();

    private int interceptorIndex = -1;

    /**
     * Create a new MessageHandlerExecutionChain.
     *
     * @param handler the handler object to execute
     */
    public MessageHandlerExecutionChain(MessageHandler handler) {
        this(handler, (MessageInterceptor[]) null);
    }

    /**
     * Create a new MessageHandlerExecutionChain.
     *
     * @param handler      the handler object to execute
     * @param interceptors the array of interceptors to apply
     *                     (in the given order) before the handler itself executes
     */
    public MessageHandlerExecutionChain(MessageHandler handler, @Nullable MessageInterceptor... interceptors) {
        this(handler, (interceptors != null ? Arrays.asList(interceptors) : Collections.emptyList()));
    }

    /**
     * Create a new MessageHandlerExecutionChain.
     *
     * @param handler         the handler object to execute
     * @param interceptorList the list of interceptors to apply
     *                        (in the given order) before the handler itself executes
     * @since 5.3
     */
    public MessageHandlerExecutionChain(MessageHandler handler, List<MessageInterceptor> interceptorList) {
        if (handler instanceof MessageHandlerExecutionChain originalChain) {
            this.handler = originalChain.getHandler();
            this.interceptorList.addAll(originalChain.interceptorList);
        } else {
            this.handler = handler;
        }
        this.interceptorList.addAll(interceptorList);
    }

    /**
     * Add the given interceptor to the end of this chain.
     */
    public void addInterceptor(MessageInterceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    /**
     * Add the given interceptor at the specified index of this chain.
     *
     * @since 5.2
     */
    public void addInterceptor(int index, MessageInterceptor interceptor) {
        this.interceptorList.add(index, interceptor);
    }

    /**
     * Add the given interceptors to the end of this chain.
     */
    public void addInterceptors(MessageInterceptor... interceptors) {
        CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
    }

    /**
     * Return the list of interceptors to apply (in the given order).
     *
     * @return the list of MessageInterceptors instances (potentially empty)
     */
    public List<MessageInterceptor> getInterceptorList() {
        return (!this.interceptorList.isEmpty() ? List.copyOf(this.interceptorList) : List.of());
    }


    /**
     * Apply preHandle methods of registered interceptors.
     *
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else return {@code false}.
     */
    boolean applyPreHandle(Message<?> message) throws Exception {
        for (int i = 0; i < this.interceptorList.size(); i++) {
            MessageInterceptor interceptor = this.interceptorList.get(i);
            if (!interceptor.preHandle(message, this.handler)) {
                triggerAfterCompletion(message, null);
                return false;
            }
            this.interceptorIndex = i;
        }
        return true;
    }

    /**
     * Apply postHandle methods of registered interceptors.
     */
    void applyPostHandle(Message<?> message)  throws Exception {

        for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
            MessageInterceptor interceptor = this.interceptorList.get(i);
            interceptor.postHandle(message, this.handler);
        }
    }

    /**
     * Trigger afterCompletion callbacks on the mapped MessageInterceptors.
     * Will just invoke afterCompletion for all interceptors whose preHandle invocation
     * has successfully completed and returned true.
     */
    void triggerAfterCompletion(Message<?> message, @Nullable Exception ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            MessageInterceptor interceptor = this.interceptorList.get(i);
            try {
                interceptor.afterCompletion(message, this.handler, ex);
            } catch (Throwable ex2) {
                log.error("MessageInterceptor.afterCompletion threw exception", ex2);
            }
        }
    }

    /**
     * Delegates to the handler's {@code toString()} implementation.
     */
    @Override
    public String toString() {
        return "MessageHandlerExecutionChain with [" + getHandler() + "] and " + this.interceptorList.size() + " interceptors";
    }
}
