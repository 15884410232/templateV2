package com.levy.collection.config.oss;

import com.dtsw.collection.service.store.MinioStorage;
import com.dtsw.collection.service.store.Storage;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio配置类
 *
 * @author Wangwang Tang
 * @since 2024-07-31
 **/
@Configuration
@ConditionalOnClass({MinioClient.class})
// @ConditionalOnProperty(value = "oss.storage", havingValue = "minio")
@EnableConfigurationProperties(MinioStorageProperties.class)
public class MinioStorageConfiguration {

    @Bean
    @SneakyThrows
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient(MinioStorageProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    @Bean
    @ConditionalOnBean({MinioClient.class})
    @ConditionalOnMissingBean({Storage.class})
    public MinioStorage minioStorage(MinioClient minioClient) {
        return new MinioStorage(minioClient);
    }
}