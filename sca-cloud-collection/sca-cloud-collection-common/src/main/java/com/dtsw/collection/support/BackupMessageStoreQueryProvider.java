package com.dtsw.collection.support;

import org.springframework.integration.jdbc.store.channel.ChannelMessageStoreQueryProvider;
import org.springframework.integration.jdbc.store.channel.PostgresChannelMessageStoreQueryProvider;

/**
 * jdbc 持久化消息SQL提供器
 *
 * @author Wangwang Tang
 * @see PostgresChannelMessageStoreQueryProvider
 * @since 2024-11-05
 */
public class BackupMessageStoreQueryProvider implements ChannelMessageStoreQueryProvider {
    @Override
    public String getCountAllMessagesInGroupQuery() {
        return "";
    }

    @Override
    public String getPollFromGroupExcludeIdsQuery() {
        return "";
    }

    @Override
    public String getPollFromGroupQuery() {
        return "";
    }

    @Override
    public String getPriorityPollFromGroupExcludeIdsQuery() {
        return "";
    }

    @Override
    public String getPriorityPollFromGroupQuery() {
        return "";
    }

    @Override
    public String getMessageQuery() {
        return "";
    }

    @Override
    public String getMessageCountForRegionQuery() {
        return "";
    }

    @Override
    public String getDeleteMessageQuery() {
        return "";
    }

    @Override
    public String getCreateMessageQuery() {
        return """
    INSERT INTO sca_cloud_collection_task_chunk (id, task_id, parent_id, parent_ids, timestamp, region, headers, payload, message, channel, status, created_time)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""";
    }

    @Override
    public String getDeleteMessageGroupQuery() {
        return "";
    }
}
