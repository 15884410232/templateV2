package com.levy.collection.flow.java.collector.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Java采集记录
 *
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@Data
@TableName("sca_cloud_java_collector_record")
public class JavaCollectorRecord {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String taskId;

    private String indexId;

    private Boolean incremental;

    private Integer firstIncremental;

    private Integer lastIncremental;

}
