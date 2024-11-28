package com.levy.dto.collection.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 开源软件表
 * @author Wangwang Tang
 * @since 2024-09-02
 */
@Data
@ToString(callSuper = true)
@TableName(value = "sca_cloud_open_source_software_extend")
public class OpenSourceSoftwareExtend {

    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 审计字段-数据创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 审计字段-更新时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;
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
