package com.dtsw.integration.support;

import com.dtsw.integration.annotation.MessageInterceptor;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Wangwang Tang
 * @since 2024-11-04
 */
public class MessageInterceptorRegistry {

    private final List<MessageInterceptorRegistration> registrations = new ArrayList<>();

    /**
     * Adds the provided {@link MessageInterceptor}.
     *
     * @param interceptor the interceptor to add
     * @return an {@link MessageInterceptorRegistration}.
     */
    public MessageInterceptorRegistration addInterceptor(MessageInterceptor interceptor) {
        MessageInterceptorRegistration registration = new MessageInterceptorRegistration(interceptor);
        this.registrations.add(registration);
        return registration;
    }

    /**
     * Return all registered interceptors.
     */
    protected List<MessageInterceptor> getInterceptors() {
        return this.registrations.stream()
                .sorted(INTERCEPTOR_ORDER_COMPARATOR)
                .map(MessageInterceptorRegistration::getInterceptor)
                .toList();
    }


    private static final Comparator<Object> INTERCEPTOR_ORDER_COMPARATOR =
            OrderComparator.INSTANCE.withSourceProvider(object -> {
                if (object instanceof MessageInterceptorRegistration interceptorRegistration) {
                    return (Ordered) interceptorRegistration::getOrder;
                }
                return null;
            });

}
