package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sca_cloud_open_source_software")
public class OpenSourceSoftware implements Serializable {

    @TableId("id")
    private String openSourceSoftwareId;

    @TableField("artifact_id")
    private String artifactId;

    @TableField("author")
    private String author;

    @TableField("contributors")
    private String contributors;

    @TableField("dependencies")
    private String dependencies;

    @TableField("description")
    private String description;

    @TableField("documentation_url")
    private String documentationUrl;

    @TableField("_extend_country")
    private String extendCountry;

    @TableField("_extend_cryptoids")
    private String extendCryptoids;

    @TableField("_extend_eccn")
    private String extendEccn;

    @TableField("group_id")
    private String groupId;

    @TableField("has_javadoc")
    private String hasJavadoc;

    @TableField("has_sources")
    private String hasSources;

    @TableField("homepage_url")
    private String homepageUrl;

    @TableField("last_updated")
    private Long lastUpdated;

    @TableField("license")
    private String license;

    @TableField("name")
    private String name;

    @TableField("organization")
    private String organization;

    @TableField("parent_id")
    private String parentId;

    @TableField("programming_language")
    private String programmingLanguage;

    @TableField("release_date")
    private String releaseDate;

    @TableField("repository_url")
    private String repositoryUrl;

    @TableField("source_code_url")
    private String sourceCodeUrl;

    @TableField("status")
    private Integer status;

    @TableField("tags")
    private String tags;

    @TableField("time_created")
    private Long timeCreated;

    @TableField("time_updated")
    private Long timeUpdated;

    @TableField("version")
    private String version;

    @TableField("product_id")
    private String productId;

}
