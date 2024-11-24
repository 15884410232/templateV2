package com.dtsw.collection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtsw.collection.entity.base.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 开源软件表
 * @author Wangwang Tang
 * @since 2024-09-02
 */
@Data
@ToString(callSuper = true)
@TableName(value = "sca_cloud_open_source_software_extend")
public class OpenSourceSoftwareExtend extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 名称
     */
    @TableField
    private String name;

    /**
     * 编程语言
     */
    @TableField
    private String programmingLanguage;

    /**
     * 联系方式：JS
     */
    @TableField
    private String contactWay;

    /**
     * 作者信息：JS
     */
    @TableField
    private String authorInfo;

    /**
     * bug地址：Python
     */
    @TableField
    private String bugTrackUrl;

    /**
     * 许可证全称：Python
     */
    @TableField
    private String licenseFullName;

    /**
     * 许可证简称：Python
     */
    @TableField
    private String licenseShortName;

    /**
     * 下载地址：Python
     */
    @TableField
    private String downloadUrl;

    /**
     * 发布信息：Python
     */
    @TableField
    private String releases;

    /**
     * 漏洞信息：Python
     */
    @TableField
    private String vulnerabilities;

}
