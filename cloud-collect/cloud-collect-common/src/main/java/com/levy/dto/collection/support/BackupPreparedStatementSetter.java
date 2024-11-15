package com.levy.dto.collection.support;

import com.dtsw.collection.enumeration.TaskChunkStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.jdbc.store.channel.ChannelMessageStorePreparedStatementSetter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Optional;

import static com.dtsw.integration.handler.AbstractMessageHandler.PARENT_ID;
import static com.dtsw.integration.handler.AbstractMessageHandler.PARENT_IDS;

/**
 * 对jdbc的查询语句注入参数
 *
 * @author Wangwang Tang
 * @since 2024-11-05
 */
@Slf4j
public class BackupPreparedStatementSetter extends ChannelMessageStorePreparedStatementSetter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String TASK_ID = "taskId";

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void setValues(PreparedStatement ps, Message<?> message, Object groupId, String region, boolean priorityEnabled) throws SQLException {
        MessageHeaders headers = message.getHeaders();
        if (headers.getId() == null) {
            throw new SQLException("id is null");
        }
        ps.setObject(1, headers.getId());
        ps.setObject(2, Optional.ofNullable(headers.get(TASK_ID)).map(Object::toString).orElse(null));
        ps.setObject(3, Optional.ofNullable(headers.get(PARENT_ID)).map(Object::toString).orElse(null));
        ps.setObject(4, toJson(headers.get(PARENT_IDS)));
        ps.setObject(5, Optional.ofNullable(headers.getTimestamp()).map(Timestamp::new).orElse(null));
        ps.setObject(6, region);
        ps.setObject(7, toJson(new LinkedHashMap<>(message.getHeaders())));
        ps.setObject(8, toJson(message.getPayload()));
        ps.setObject(9, serializingMessage(message));
        ps.setObject(10, Optional.ofNullable(groupId).map(Object::toString).orElse(null));
        ps.setObject(11, TaskChunkStatus.READY.getValue());
        ps.setObject(12, LocalDateTime.now());
    }

    private static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("serialize {} to json error ", obj, e);
            throw new RuntimeException(e);
        }
    }

    private static String serializingMessage(Message<?> message) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(message);
            byte[] bytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("serialize message fail", e);
        }
    }
}
