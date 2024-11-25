package com.levy.collect.config.integration;


import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.collection.support.BackupMessageStoreQueryProvider;
import com.levy.dto.collection.support.BackupPreparedStatementSetter;
import com.levy.dto.integration.annotation.IntegrationConfigurer;
import com.levy.dto.integration.store.RedisChannelPriorityBackupMessageStore;
import com.levy.dto.integration.support.MessageChannelRegistrar;
import com.levy.dto.integration.support.MessageChannelRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class IntegrationConfig implements IntegrationConfigurer {


    @Override
    public void addMessageChannelRegistrar(MessageChannelRegistry registrar) {
        Set<String> channels = new HashSet<>();
        for (FlowChannel value : FlowChannel.values()) {
            if(value.name().contains("GATEWAY")){
                channels.add(value.getChannel());
            }
        }
        registrar.addChannel(channels);
    }

    @Bean
    public JdbcChannelMessageStore jdbcChannelMessageStore(DataSource dataSource) {
        JdbcChannelMessageStore store = new JdbcChannelMessageStore(dataSource);
        store.setPreparedStatementSetter(new BackupPreparedStatementSetter());
        store.setChannelMessageStoreQueryProvider(new BackupMessageStoreQueryProvider());
        return store;
    }

    @Bean
    public RedisChannelPriorityBackupMessageStore redisChannelBackupMessageStore(RedisConnectionFactory redisConnectionFactory,
                                                                                 JdbcChannelMessageStore backupMessageStore) {
        return new RedisChannelPriorityBackupMessageStore(redisConnectionFactory, backupMessageStore);
    }

    @Bean
    public MessageChannelRegistrar messageChannelRegistrar(RedisChannelPriorityBackupMessageStore messageStore) {
        return new MessageChannelRegistrar(messageStore);
    }


}
