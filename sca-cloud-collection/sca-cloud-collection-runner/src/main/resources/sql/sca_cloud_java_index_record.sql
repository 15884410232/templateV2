create table sca_cloud_java_index_record
(
    id                                    VARCHAR(36) NOT NULL PRIMARY KEY,
    task_id                               VARCHAR(36),
    type                                  VARCHAR(20),
    repository_id                         TEXT,
    all_groups                            TEXT,
    root_groups                           TEXT,
    record_modified                       TIMESTAMP,
    group_id                              TEXT,
    artifact_id                           TEXT,
    version                               TEXT,
    classifier                            TEXT,
    packaging                             TEXT,
    file_extension                        TEXT,
    file_modified                         TIMESTAMP,
    file_size                             BIGINT,
    has_sources                           BOOLEAN,
    has_javadoc                           BOOLEAN,
    has_signature                         BOOLEAN,
    name                                  TEXT,
    description                           TEXT,
    sha1                                  VARCHAR(255),
    sha256                                VARCHAR(255),
    class_names                           TEXT,
    plugin_prefix                         TEXT,
    plugin_goals                          TEXT,
    bundle_symbolic_name                  TEXT,
    bundle_version                        TEXT,
    export_package                        TEXT,
    export_service                        TEXT,
    bundle_description                    TEXT,
    bundle_name                           TEXT,
    bundle_license                        TEXT,
    bundle_doc_url                        TEXT,
    import_package                        TEXT,
    require_bundle                        TEXT,
    provide_capability                    TEXT,
    require_capability                    TEXT,
    fragment_host                         TEXT,
    bundle_required_execution_environment TEXT
) PARTITION BY HASH (id);

comment on table sca_cloud_java_index_record is 'Java索引记录表';
comment on column sca_cloud_java_index_record.id is '索引ID';
comment on column sca_cloud_java_index_record.task_id is '任务ID';
comment on column sca_cloud_java_index_record.type is '索引类型';
comment on column sca_cloud_java_index_record.repository_id is '仓库ID';
comment on column sca_cloud_java_index_record.all_groups is '所有组列表';
comment on column sca_cloud_java_index_record.root_groups is '根组列表';
comment on column sca_cloud_java_index_record.record_modified is '记录修改时间（添加到索引或从索引中删除）';
comment on column sca_cloud_java_index_record.group_id is '制品组ID';
comment on column sca_cloud_java_index_record.artifact_id is '制品ID';
comment on column sca_cloud_java_index_record.version is '版本号';
comment on column sca_cloud_java_index_record.classifier is '分类';
comment on column sca_cloud_java_index_record.packaging is '打包方式';
comment on column sca_cloud_java_index_record.file_extension is '文件扩展名';
comment on column sca_cloud_java_index_record.file_modified is '文件修改时间';
comment on column sca_cloud_java_index_record.file_size is '文件大小（字节）';
comment on column sca_cloud_java_index_record.has_sources is '是否含有sources';
comment on column sca_cloud_java_index_record.has_javadoc is '是否含有javadoc';
comment on column sca_cloud_java_index_record.has_signature is '是否含有签名';
comment on column sca_cloud_java_index_record.name is '名称';
comment on column sca_cloud_java_index_record.description is '描述';
comment on column sca_cloud_java_index_record.sha1 is 'sha1摘要';
comment on column sca_cloud_java_index_record.sha256 is 'sha256摘要';
comment on column sca_cloud_java_index_record.class_names is '类名';
comment on column sca_cloud_java_index_record.plugin_prefix is '插件前缀';
comment on column sca_cloud_java_index_record.plugin_goals is '插件目标';
comment on column sca_cloud_java_index_record.bundle_symbolic_name is 'OSGi "Bundle-SymbolicName" manifest';
comment on column sca_cloud_java_index_record.bundle_version is 'OSGi "Bundle-Version" manifest';
comment on column sca_cloud_java_index_record.export_package is 'OSGi "Export-Package" manifest';
comment on column sca_cloud_java_index_record.export_service is 'OSGi "Export-Service" manifest';
comment on column sca_cloud_java_index_record.bundle_description is 'OSGi "Bundle-Description" manifest';
comment on column sca_cloud_java_index_record.bundle_name is 'OSGi "Bundle-Name" manifest';
comment on column sca_cloud_java_index_record.bundle_license is 'OSGi "Bundle-License" manifest';
comment on column sca_cloud_java_index_record.bundle_doc_url is 'OSGi "Bundle-DocURL" manifest';
comment on column sca_cloud_java_index_record.import_package is 'OSGi "Import-Package" manifest ';
comment on column sca_cloud_java_index_record.require_bundle is 'OSGi "Require-Bundle" manifest';
comment on column sca_cloud_java_index_record.provide_capability is 'OSGi "Provide-Capability" manifest';
comment on column sca_cloud_java_index_record.require_capability is 'OSGi "Require-Capability" manifest';
comment on column sca_cloud_java_index_record.fragment_host is 'OSGi "Fragment-Host" manifest';
comment on column sca_cloud_java_index_record.bundle_required_execution_environment is 'OSGi "Bundle-RequiredExecutionEnvironment" manifest';

DO
$$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 0..99
            LOOP
                EXECUTE format('CREATE TABLE sca_cloud_java_index_record_p%s PARTITION OF sca_cloud_java_index_record FOR VALUES WITH (MODULUS 100, REMAINDER %s);', i, i);
            END LOOP;
    END
$$;

create index sca_cloud_java_index_record_type_index on sca_cloud_java_index_record (type);
create index sca_cloud_java_index_record_group_id_index on sca_cloud_java_index_record (group_id, artifact_id, version);
create index sca_cloud_java_index_record_artifact_id_index on sca_cloud_java_index_record (artifact_id, version);
create index sca_cloud_java_index_record_version_index on sca_cloud_java_index_record (version);
create index sca_cloud_java_index_record_name_index on sca_cloud_java_index_record (name);
create index sca_cloud_java_index_record_description_index on sca_cloud_java_index_record (description);

