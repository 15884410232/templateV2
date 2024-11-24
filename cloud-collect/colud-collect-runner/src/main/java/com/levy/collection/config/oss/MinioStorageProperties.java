package com.levy.collection.config.oss;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * minio配置
 *
 * @author Wangwang Tang
 * @since 2024-07-31
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "oss.minio")
public class MinioStorageProperties {

    /** minio Api地址 */
    String endpoint = "http://localhost:9000";

    /** 用户名 */
    String accessKey;

    /** 密码 */
    String secretKey;

}
