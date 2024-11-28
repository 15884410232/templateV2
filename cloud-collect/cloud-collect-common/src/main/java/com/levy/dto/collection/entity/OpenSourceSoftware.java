package com.levy.dto.collection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 开源软件表
 */
@Data
@ToString(callSuper = true)
@TableName(value ="sca_cloud_open_source_software")
public class OpenSourceSoftware implements Serializable{
    /**
     * ID，组件唯一标识，根据的唯一属性集计算
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 产品ID，与版本无关的组件唯一标识
     */
    private String productId;

    /**
     * 名称
     */
    private String name;

    /**
     * groupId
     */
    private String groupId;

    /**
     * artifactId
     */
    private String artifactId;

    /**
     * 版本
     */
    private String version;

    /**
     * 许可证
     */
    private String license;

    /**
     * 组件编程语言
     */
    private String programmingLanguage;

    /**
     * 组件描述信息
     */
    private String description;

    /**
     * 组件发布日期
     */
    private String releaseDate;

    /**
     * 作者
     */
    private String author;

    /**
     * 作者邮箱
     */
    private String authorEmail;

    /**
     * 作者主页地址
     */
    private String authorUrl;

    /**
     * 主页地址
     */
    private String homepageUrl;

    /**
     * 仓库地址
     */
    private String repositoryUrl;

    /**
     * 源码地址
     */
    private String sourceCodeUrl;

    /**
     * 文档地址
     */
    private String documentationUrl;

    /**
     * 组件标签
     */
    private String tags;

    /**
     * 组件被依赖信息
     */
    private String dependents;

    /**
     * 组件依赖信息
     */
    private String dependencies;

    /**
     * 组件贡献者
     */
    private String contributors;

    /**
     * 组件最后更新时间
     */
    private Long lastUpdated;

    /**
     * 组织
     */
    private String organization;

    /**
     * 组件状态：1-启用 2-禁用（移除）
     */
    private Integer status;

    /**
     * 父组件ID
     */
    private String parentId;

    /**
     * 组件创建时间
     */
    private Long timeCreated;

    /**
     * 组件更新时间
     */
    private Long timeUpdated;

    /**
     * 组件是否发布源码
     */
    private Boolean hasSources;

    /**
     * 组件是否发布javadoc文档
     */
    private Boolean hasJavadoc;

    /**
     * 组件打包方式
     */
    private String packaging;

    /**
     * 组件开始年份
     */
    private String inceptionYear;

    /**
     * 组件来源国家
     */
    @TableField("_extend_country")
    private String extendCountry;

    /**
     * 组件对应国家的出口管制编码
     */
    @TableField("_extend_eccn")
    private String extendEccn;

    /**
     * 组件加密（未完全定义）
     */
    @TableField("_extend_cryptoids")
    private String extendCryptoids;

}
