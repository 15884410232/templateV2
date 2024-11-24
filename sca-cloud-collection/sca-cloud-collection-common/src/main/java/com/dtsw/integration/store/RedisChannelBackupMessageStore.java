package com.dtsw.integration.store;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.store.RedisChannelMessageStore;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

/**
 * Specialized Redis {@link RedisChannelMessageStore} that uses a list to back a QueueChannel.
 * Messages can be backed up.
 * <p>
 * Requires {@link #setBeanName(String)} which is used as part of the key.
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Getter
@Setter
public class RedisChannelBackupMessageStore extends RedisChannelMessageStore {

    private ChannelMessageStore backupMessageStore;

    public RedisChannelBackupMessageStore(RedisConnectionFactory connectionFactory, ChannelMessageStore backupMessageStore) {
        super(connectionFactory);
        this.backupMessageStore = backupMessageStore;
    }

    @Override
    @Transactional
    public MessageGroup addMessageToGroup(Object groupId, Message<?> message) {
        backupMessageStore.addMessageToGroup(groupId, message);
        return super.addMessageToGroup(groupId, message);
    }
}
