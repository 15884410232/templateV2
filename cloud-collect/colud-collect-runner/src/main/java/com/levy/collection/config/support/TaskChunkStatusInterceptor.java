package com.levy.collection.config.support;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtsw.collection.entity.TaskChunk;
import com.dtsw.collection.enumeration.TaskChunkStatus;
import com.dtsw.collection.mapper.TaskChunkMapper;
import com.dtsw.integration.annotation.MessageInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Wangwang Tang
 * @since 2024-11-05
 */
@Slf4j
@RequiredArgsConstructor
public class TaskChunkStatusInterceptor implements MessageInterceptor {

    private final TaskChunkMapper taskChunkMapper;

    @Override
    public boolean preHandle(Message<?> message, Object handler) {
        UUID id = message.getHeaders().getId();
        if (id != null) {
            try {
                taskChunkMapper.update(Wrappers.lambdaUpdate(TaskChunk.class)
                        .eq(TaskChunk::getId, id.toString())
                        .set(TaskChunk::getStatus, TaskChunkStatus.RUNNING)
                        .set(TaskChunk::getStartTime, LocalDateTime.now()));
            } catch (Exception e) {
                log.error("start task chunk[{}] error.", id, e);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(Message<?> message, Object handler, Exception ex) {
        if (ex != null) {
            log.error("Handler message[{}] error: {}", message, ex.getMessage(), ex);
        }
        UUID id = message.getHeaders().getId();
        if (id == null) {
            return;
        }
        try {
            if (ex == null) {
                taskChunkMapper.update(Wrappers.lambdaUpdate(TaskChunk.class)
                        .eq(TaskChunk::getId, id.toString())
                        .set(TaskChunk::getStatus, TaskChunkStatus.SUCCESS)
                        .set(TaskChunk::getStopTime, LocalDateTime.now()));
            } else {
                taskChunkMapper.update(Wrappers.lambdaUpdate(TaskChunk.class)
                        .eq(TaskChunk::getId, id.toString())
                        .set(TaskChunk::getStatus, TaskChunkStatus.FAIL)
                        .set(TaskChunk::getStopTime, LocalDateTime.now())
                        .set(TaskChunk::getReason, ex.getMessage()));
            }
        } catch (Exception e) {
            log.error("stop task chunk[{}] error.", id, e);
        }
    }

}
