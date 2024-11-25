package com.levy.collection.config;


import com.levy.collection.config.support.TaskChunkStatusInterceptor;
import com.levy.collection.mapper.TaskChunkMapper;
import com.levy.dto.collection.support.BackupMessageStoreQueryProvider;
import com.levy.dto.collection.support.BackupPreparedStatementSetter;
import com.levy.dto.integration.annotation.IntegrationConfigurer;
import com.levy.dto.integration.store.RedisChannelPriorityBackupMessageStore;
import com.levy.dto.integration.support.MessageChannelRegistrar;
import com.levy.dto.integration.support.MessageHandlerRegistrar;
import com.levy.dto.integration.support.MessageInterceptorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class IntegrationConfig implements IntegrationConfigurer {

    private final TaskChunkMapper taskChunkMapper;

    @Override
    public void addInterceptors(MessageInterceptorRegistry registry) {
        registry.addInterceptor(new TaskChunkStatusInterceptor(taskChunkMapper)).order(Integer.MIN_VALUE);
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

    @Bean
    public MessageHandlerRegistrar messageHandlerRegistrar() {
        return new MessageHandlerRegistrar();
    }

}
