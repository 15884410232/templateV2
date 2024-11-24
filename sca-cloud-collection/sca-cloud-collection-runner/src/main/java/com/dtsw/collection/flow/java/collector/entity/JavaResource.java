package com.dtsw.collection.flow.java.collector.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Wangwang Tang
 * @since 2024-11-10
 */
@Data
@TableName("sca_cloud_java_resource")
public class JavaResource {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String taskId;

    private String name;

    private String fullName;

    private String parentId;

    private String url;

    private LocalDateTime timestamp;

    private Long size;

    private Boolean hasChild;

    private Integer child;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
