create table sca_cloud_java_collector_record
(
    id                VARCHAR(36) NOT NULL PRIMARY KEY,
    task_id           VARCHAR(36),
    index_id          VARCHAR(200),
    incremental       BOOLEAN,
    first_incremental INTEGER,
    last_incremental  INTEGER
);

comment on table sca_cloud_java_collector_record is 'Java采集记录';
comment on column sca_cloud_java_collector_record.id is '采集ID';
comment on column sca_cloud_java_collector_record.task_id is '任务ID';
comment on column sca_cloud_java_collector_record.index_id is '索引ID';
comment on column sca_cloud_java_collector_record.incremental is '是否增量采集';
comment on column sca_cloud_java_collector_record.first_incremental is '起始的增量索引（当增量采集时）';
comment on column sca_cloud_java_collector_record.last_incremental is '最后的增量索引';


create index sca_cloud_java_collector_record_index_id_index on sca_cloud_java_collector_record (index_id);

