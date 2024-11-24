package com.dtsw.collection.flow.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MinioSaveObject implements Serializable {

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
