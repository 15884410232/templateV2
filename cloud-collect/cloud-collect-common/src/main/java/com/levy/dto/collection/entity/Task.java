package com.levy.dto.collection.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import com.levy.dto.collection.entity.base.BaseEntity;
import com.levy.dto.collection.enumeration.TaskStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sca_cloud_collection_task", autoResultMap = true)
public class Task extends BaseEntity {

    /**
     * 软件源ID
     */
    private String sourceId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务状态
     */
    @EnumValue
    private TaskStatus status;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 任务参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> params;

    /**
     * 开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 子任务总数
     */
    private Long totalCount;

    /**
     * 子任务成功数
     */
    private Long successCount;

    /**
     * 子任务失败数
     */
    private Long failedCount;
}
