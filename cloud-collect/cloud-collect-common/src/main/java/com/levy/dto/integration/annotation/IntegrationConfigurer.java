package com.levy.dto.integration.annotation;

import com.levy.dto.integration.support.MessageChannelRegistry;
import com.levy.dto.integration.support.MessageInterceptorRegistry;

/**
 * Integration配置回调方法，定义默认配置
 *
 * @author Wangwang Tang
 * @since 2024-11-04
 */
public interface IntegrationConfigurer {

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of handle messages.
     * Interceptors can be registered to apply to all messages.
     */
    default void addInterceptors(MessageInterceptorRegistry registry) {
    }

    default void addMessageChannelRegistrar(MessageChannelRegistry registrar) {
    }

}
