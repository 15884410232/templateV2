package com.dtsw.collection.flow.go.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dtsw.collection.entity.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value ="sca_cloud_go_module")
public class GoModule extends BaseEntity {

    /**
     * 模块名称
     */
    private  String name;
    /**
     * 版本号
     */
    private  String version;
    /**
     * 发布时间
     */
    private  LocalDateTime release_time;
    /**
     * 任务Id
     */
    private  String  task_id;

}
