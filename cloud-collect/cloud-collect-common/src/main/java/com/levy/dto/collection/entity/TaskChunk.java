package com.levy.dto.collection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.levy.dto.collection.enumeration.TaskChunkStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@Data
@TableName(value = "sca_cloud_collection_task_chunk", autoResultMap = true)
public class TaskChunk {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 任务ID */
    private String taskId;

    /** 父ID */
    private String parentId;

    /** 父ID集合 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> parentIds;

    /** 消息时间戳 */
    private LocalDateTime timestamp;

    /** 区域 */
    private String region;

    /** 消息头 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> headers;

    /** 消息载体 */
    private String payload;

    /** 消息（序列化后） */
    private String message;

    /** 消息管道 */
    private String channel;

    /** 消息状态 */
    private TaskChunkStatus status;

    /** 创建时间 */
    private LocalDateTime createdTime;

    /** 启动时间 */
    private LocalDateTime startTime;

    /** 停止时间 */
    private LocalDateTime stopTime;

    /** 失败原因 */
    private String reason;
}
