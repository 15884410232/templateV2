package com.dtsw.collection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dtsw.collection.entity.base.OpenSourceSoftwareBase;
import lombok.Data;
import lombok.ToString;

/**
 * 开源软件表
 */
@Data
@ToString(callSuper = true)
@TableName(value ="sca_cloud_open_source_software",autoResultMap = true)
public class OpenSourceSoftware extends OpenSourceSoftwareBase {

}
