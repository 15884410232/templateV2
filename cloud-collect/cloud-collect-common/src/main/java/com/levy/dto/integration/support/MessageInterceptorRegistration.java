package com.levy.dto.integration.support;


import com.levy.dto.integration.annotation.MessageInterceptor;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * @author Wangwang Tang
 * @since 2024-11-04
 */
public class MessageInterceptorRegistration {

    @Getter(AccessLevel.PROTECTED)
    private final MessageInterceptor interceptor;

    private int order = 0;

    public MessageInterceptorRegistration(MessageInterceptor interceptor) {
        Assert.notNull(interceptor, "Interceptor is required");
        this.interceptor = interceptor;
    }

    /**
     * Specify an order position to be used. Default is 0.
     *
     */
    public MessageInterceptorRegistration order(int order) {
        this.order = order;
        return this;
    }

    /**
     * Return the order position to be used.
     */
    protected int getOrder() {
        return this.order;
    }
}
