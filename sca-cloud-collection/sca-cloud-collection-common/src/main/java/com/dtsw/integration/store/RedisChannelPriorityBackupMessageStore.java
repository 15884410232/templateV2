package com.dtsw.integration.store;

import com.dtsw.integration.annotation.BaseIntegrationConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.redis.store.RedisChannelPriorityMessageStore;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.PriorityCapableChannelMessageStore;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

/**
 * Specialized Redis {@link PriorityCapableChannelMessageStore} that uses lists to back a QueueChannel.
 * Messages are removed in priority order ({@link IntegrationMessageHeaderAccessor#PRIORITY}).
 * Priorities 0-9 are supported (9 the highest); invalid priority values are treated with the same priority (none)
 * as messages with no priority header (retrieved after any messages that have a priority).
 * Messages can be backed up.
 * <p>
 * Requires that groupId is a String.
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Slf4j
@Getter
@Setter
public class RedisChannelPriorityBackupMessageStore extends RedisChannelPriorityMessageStore {

    /**
     * Backup able message storage.
     */
    private ChannelMessageStore backupMessageStore;

    private BaseIntegrationConfig baseIntegrationConfig;

    public RedisChannelPriorityBackupMessageStore(RedisConnectionFactory connectionFactory, ChannelMessageStore backupMessageStore) {
        super(connectionFactory);
        this.backupMessageStore = backupMessageStore;
    }

    public RedisChannelPriorityBackupMessageStore(RedisConnectionFactory connectionFactory, ChannelMessageStore backupMessageStore,BaseIntegrationConfig baseIntegrationConfig) {
        super(connectionFactory);
        this.backupMessageStore = backupMessageStore;
        this.baseIntegrationConfig = baseIntegrationConfig;
    }

    @Transactional
    @Override
    public MessageGroup addMessageToGroup(Object groupId, Message<?> message) {
//        backupMessageStore.addMessageToGroup(groupId, message);
        return super.addMessageToGroup(groupId, message);
    }

    @Override
    public Message<?> pollMessageFromGroup(Object groupId) {

        if (baseIntegrationConfig != null&&
                baseIntegrationConfig.getPauseChannel()!=null){
            if(baseIntegrationConfig.getPauseChannel().contains(groupId)){
                return null;
            }else if(BaseIntegrationConfig.pauseAll.equals(baseIntegrationConfig.getPasuseAllChannel())){
                return null;
            }
        }
        Message<?> message=null;
        try {
            message = super.pollMessageFromGroup(groupId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return message;
    }
}
