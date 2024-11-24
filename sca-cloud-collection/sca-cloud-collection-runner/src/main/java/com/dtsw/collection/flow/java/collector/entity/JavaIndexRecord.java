package com.dtsw.collection.flow.java.collector.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.maven.index.reader.Record;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * @author Wangwang Tang
 * @since 20241107
 */
@Data
@NoArgsConstructor
@TableName(value = "sca_cloud_java_index_record", autoResultMap = true)
public class JavaIndexRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String taskId;

    @TableField(typeHandler = EnumTypeHandler.class)
    private Record.Type type;

    private String repositoryId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> allGroups;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> rootGroups;

    private LocalDateTime recordModified;

    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String packaging;

    private String fileExtension;

    private LocalDateTime fileModified;

    private Long fileSize;

    private Boolean hasSources;

    private Boolean hasJavadoc;

    private Boolean hasSignature;

    private String name;

    private String description;

    @TableField("sha1")
    private String sha1;

    @TableField("sha256")
    private String sha256;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> classNames;

    private String pluginPrefix;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> pluginGoals;

    private String bundleSymbolicName;

    private String bundleVersion;

    private String exportPackage;

    private String exportService;

    private String bundleDescription;

    private String bundleName;

    private String bundleLicense;

    private String bundleDocUrl;

    private String importPackage;

    private String requireBundle;

    private String provideCapability;

    private String requireCapability;

    private String fragmentHost;

    private String bundleRequiredExecutionEnvironment;

    public JavaIndexRecord(Record record) {
        this.type = record.getType();
        this.repositoryId = record.getString(Record.REPOSITORY_ID);
        this.allGroups = Optional.ofNullable(record.getStringArray(Record.ALL_GROUPS)).map(List::of).orElseGet(List::of);
        this.rootGroups = Optional.ofNullable(record.getStringArray(Record.ROOT_GROUPS)).map(List::of).orElseGet(List::of);
        Long recordModified = record.getLong(Record.REC_MODIFIED);
        if (recordModified != null && recordModified > 0) {
            this.recordModified = Instant.ofEpochMilli(recordModified).atOffset(ZoneOffset.UTC).toLocalDateTime();
        }
        this.groupId = record.getString(Record.GROUP_ID);
        this.artifactId = record.getString(Record.ARTIFACT_ID);
        this.version = record.getString(Record.VERSION);
        this.classifier = record.getString(Record.CLASSIFIER);
        this.packaging = record.getString(Record.PACKAGING);
        this.fileExtension = record.getString(Record.FILE_EXTENSION);
        Long fileModified = record.getLong(Record.FILE_MODIFIED);
        if (fileModified != null && fileModified > 0) {
            this.fileModified = Instant.ofEpochMilli(fileModified).atOffset(ZoneOffset.UTC).toLocalDateTime();
        }
        this.fileSize = record.getLong(Record.FILE_SIZE);
        this.hasSources = record.getBoolean(Record.HAS_SOURCES);
        this.hasJavadoc = record.getBoolean(Record.HAS_JAVADOC);
        this.hasSignature = record.getBoolean(Record.HAS_SIGNATURE);
        this.name = record.getString(Record.NAME);
        this.description = record.getString(Record.DESCRIPTION);
        this.sha1 = record.getString(Record.SHA1);
        this.sha256 = record.getString(Record.SHA_256);
        this.classNames = Optional.ofNullable(record.getStringArray(Record.CLASSNAMES)).map(List::of).orElseGet(List::of);
        this.pluginPrefix = record.getString(Record.PLUGIN_PREFIX);
        this.pluginGoals = Optional.ofNullable(record.getStringArray(Record.PLUGIN_GOALS)).map(List::of).orElseGet(List::of);
        this.bundleSymbolicName = record.getString(Record.OSGI_BUNDLE_SYMBOLIC_NAME);
        this.bundleVersion = record.getString(Record.OSGI_BUNDLE_VERSION);
        this.exportPackage = record.getString(Record.OSGI_EXPORT_PACKAGE);
        this.exportService = record.getString(Record.OSGI_EXPORT_SERVICE);
        this.bundleDescription = record.getString(Record.OSGI_BUNDLE_DESCRIPTION);
        this.bundleName = record.getString(Record.OSGI_BUNDLE_NAME);
        this.bundleLicense = record.getString(Record.OSGI_BUNDLE_LICENSE);
        this.bundleDocUrl = record.getString(Record.OSGI_EXPORT_DOCURL);
        this.importPackage = record.getString(Record.OSGI_IMPORT_PACKAGE);
        this.requireBundle = record.getString(Record.OSGI_REQUIRE_BUNDLE);
        this.provideCapability = record.getString(Record.OSGI_PROVIDE_CAPABILITY);
        this.requireCapability = record.getString(Record.OSGI_REQUIRE_CAPABILITY);
        this.fragmentHost = record.getString(Record.OSGI_FRAGMENT_HOST);
        this.bundleRequiredExecutionEnvironment = record.getString(Record.OSGI_BREE);
    }
}
