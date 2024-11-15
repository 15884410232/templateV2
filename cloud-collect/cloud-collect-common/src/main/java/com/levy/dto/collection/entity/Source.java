package com.levy.dto.collection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dtsw.collection.entity.base.BaseEntity;
import com.dtsw.collection.enumeration.SourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 软件源表
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(autoResultMap = true, value ="sca_cloud_collection_source")
public class Source extends BaseEntity {

    /**
     * 软件源名称
     */
    private String name;

    /**
     * 源类型
     */
    private SourceType type;

    /**
     * 采集模型
     */
    private String gateway;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 定时任务cron表达式
     */
    private String cron;

    /**
     * 是否删除
     */
    private Boolean remove;
}
