package com.dtsw.collection.config.oss;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地磁盘文件系统配置
 *
 * @author Wangwang Tang
 * @since 2024-07-31
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "oss.local")
public class LocalStorageProperties {

    /** 本地文件根目录 */
    private String path = "";

}
