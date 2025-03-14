package com.dtsw.integration.annotation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "integration.config")
public class BaseIntegrationConfig {

    public static String pauseAll="pasuse";

    /**
     * 默认线程数1
     */
    private Integer concurrency=1;

    /**
     * 暂停消费的通道
     */
    private List<String> pauseChannel;

    /**
     * 暂停消费的通道
     */
    private String pasuseAllChannel;

    private String pasuseGo;
}
