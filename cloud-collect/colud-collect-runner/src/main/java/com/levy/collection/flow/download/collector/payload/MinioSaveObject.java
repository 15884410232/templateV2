package com.levy.collection.flow.download.collector.payload;

import com.levy.dto.util.netty.BasePayload;
import lombok.Data;

import java.io.Serializable;

@Data
public class MinioSaveObject extends BasePayload implements Serializable {
    /**
     * 存储的bucketName
     */
    private String bucketName;

    /**
     * 存储的文件名
     */
    private String objectName;

    /**
     * 下载地址
     */
    private String downloadUrl;
}
