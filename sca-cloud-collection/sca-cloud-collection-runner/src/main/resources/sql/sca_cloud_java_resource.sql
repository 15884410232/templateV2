create table sca_cloud_java_resource
(
    id           VARCHAR(36) NOT NULL PRIMARY KEY,
    task_id      VARCHAR(36),
    name         TEXT,
    full_name    TEXT,
    parent_id    VARCHAR(36),
    url          TEXT,
    timestamp    TIMESTAMP,
    size         BIGINT,
    has_child    BOOLEAN,
    child        INTEGER,
    created_time TIMESTAMP,
    updated_time TIMESTAMP
) PARTITION BY HASH (name);

comment on table sca_cloud_java_resource is 'Java资源表';
comment on column sca_cloud_java_resource.id is '资源ID';
comment on column sca_cloud_java_resource.task_id is '任务ID';
comment on column sca_cloud_java_resource.name is '名称';
comment on column sca_cloud_java_resource.full_name is '全称';
comment on column sca_cloud_java_resource.parent_id is '父ID';
comment on column sca_cloud_java_resource.url is '资源路径';
comment on column sca_cloud_java_resource.timestamp is '时间戳 ';
comment on column sca_cloud_java_resource.size is '文件大小';
comment on column sca_cloud_java_resource.has_child is '是否有子项';
comment on column sca_cloud_java_resource.child is '子项数量';
comment on column sca_cloud_java_resource.created_time is '创建时间';
comment on column sca_cloud_java_resource.updated_time is '更新时间';

DO
$$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 0..99
            LOOP
                EXECUTE format(
                        'CREATE TABLE sca_cloud_java_resource_p%s PARTITION OF sca_cloud_java_resource FOR VALUES WITH (MODULUS 100, REMAINDER %s);',
                        i, i);
            END LOOP;
    END
$$;

create index sca_cloud_java_resource_name_index on sca_cloud_java_resource (name);
create unique index sca_cloud_java_resource_full_name_index on sca_cloud_java_resource (full_name);
create index sca_cloud_java_resource_parent_id_index on sca_cloud_java_resource (parent_id);
