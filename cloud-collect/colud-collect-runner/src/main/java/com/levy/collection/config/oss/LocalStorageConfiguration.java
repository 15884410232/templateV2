package com.levy.collection.config.oss;

import com.dtsw.collection.service.store.LocalStorage;
import com.dtsw.collection.service.store.Storage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author Wangwang Tang
 * @since 2024-08-02
 */
// @Configuration
// @ConditionalOnProperty(value = "oss.storage", havingValue = "local", matchIfMissing = true)
public class LocalStorageConfiguration {

    @Bean
    @ConditionalOnMissingBean(Storage.class)
    public LocalStorage localStorage(LocalStorageProperties properties) {
        return new LocalStorage(properties.getPath());
    }

}
