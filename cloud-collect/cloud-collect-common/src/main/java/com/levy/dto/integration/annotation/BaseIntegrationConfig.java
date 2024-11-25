package com.levy.dto.integration.annotation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "integration.config")
public class BaseIntegrationConfig {

    /**
     * 默认线程数1
     */
    private Integer concurrency=1;

}
