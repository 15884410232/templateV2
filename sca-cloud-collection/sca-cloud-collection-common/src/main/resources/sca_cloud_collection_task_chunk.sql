create table sca_cloud_collection_task_chunk
(
    id           VARCHAR(36) NOT NULL PRIMARY KEY,
    task_id      VARCHAR(36),
    parent_id    VARCHAR(36),
    parent_ids   TEXT,
    timestamp    TIMESTAMP,
    region       VARCHAR(255),
    headers      TEXT,
    payload      TEXT,
    message      TEXT,
    channel      VARCHAR(50),
    status       VARCHAR(20),
    created_time TIMESTAMP,
    start_time   TIMESTAMP,
    stop_time    TIMESTAMP,
    reason       TEXT
);

comment on table sca_cloud_collection_task_chunk is '采集任务块';
comment on column sca_cloud_collection_task_chunk.id is '消息ID';
comment on column sca_cloud_collection_task_chunk.task_id is '任务ID';
comment on column sca_cloud_collection_task_chunk.parent_id is '父消息ID';
comment on column sca_cloud_collection_task_chunk.parent_ids is '所有父消息ID';
comment on column sca_cloud_collection_task_chunk.timestamp is '消息时间戳';
comment on column sca_cloud_collection_task_chunk.region is '区域';
comment on column sca_cloud_collection_task_chunk.headers is '消息头';
comment on column sca_cloud_collection_task_chunk.payload is '消息载体';
comment on column sca_cloud_collection_task_chunk.message is '消息（字节BASE64编译）';
comment on column sca_cloud_collection_task_chunk.channel is '消息通道';
comment on column sca_cloud_collection_task_chunk.status is '任务状态';
comment on column sca_cloud_collection_task_chunk.created_time is '创建时间';
comment on column sca_cloud_collection_task_chunk.start_time is '启动时间';
comment on column sca_cloud_collection_task_chunk.stop_time is '停止时间';
comment on column sca_cloud_collection_task_chunk.reason is '失败原因';

create index sca_cloud_collection_task_chunk_task_id_index on sca_cloud_collection_task_chunk (task_id);
create index sca_cloud_collection_task_chunk_parent_id_index on sca_cloud_collection_task_chunk (parent_id);
create index sca_cloud_collection_task_chunk_channel_index on sca_cloud_collection_task_chunk (channel);

