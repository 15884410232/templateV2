package com.levy.collect.start;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.levy.collect.mapper.SoftwareSourceMapper;
import com.levy.collect.mapper.TaskChunkMapper;
import com.levy.collect.mapper.TaskMapper;
import com.levy.dto.collection.constant.MessageHeaderConstants;
import com.levy.dto.collection.entity.Source;
import com.levy.dto.collection.entity.Task;
import com.levy.dto.collection.entity.TaskChunk;
import com.levy.dto.collection.enumeration.TaskChunkStatus;
import com.levy.dto.collection.enumeration.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskStartup implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(TaskStartup.class);

    private final SoftwareSourceMapper softwareSourceMapper;

    private final TaskMapper taskMapper;

    private final TaskChunkMapper taskChunkMapper;

    private ApplicationContext applicationContext;

    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void startupTask() {
        LambdaQueryWrapper<Task> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Task::getStatus, TaskStatus.READY);
        List<Task> tasks = taskMapper.selectList(lambdaQueryWrapper);
        for (Task task : tasks) {
            try {
                Source source = Optional.ofNullable(softwareSourceMapper.selectById(task.getSourceId())).orElseThrow();
                Map<String, Object> params = task.getParams();
                MessageBuilder<String> stringMessageBuilder = MessageBuilder.withPayload("");
                stringMessageBuilder.setHeader(MessageHeaderConstants.TASK_ID, task.getId());
                params.forEach((key,val)->{
                    stringMessageBuilder.setHeader(key, val);
                });
                Message<String> message = stringMessageBuilder.build();
                MessageChannel channel = applicationContext.getBean(source.getGateway(), MessageChannel.class);
                channel.send(message);
                task.setStatus(TaskStatus.RUNNING);
                task.setStartedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            } catch (Exception e) {
                task.setStatus(TaskStatus.FAILED);
                task.setReason(e.getMessage());
                task.setStartedAt(LocalDateTime.now());
                task.setCompletedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            }
        }
    }

//    @Scheduled(initialDelay = 5000, fixedDelay = 50000)
    public void completeTask() {
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper=new LambdaQueryWrapper();
        taskLambdaQueryWrapper.eq(Task::getStatus, TaskStatus.RUNNING);
        List<Task> tasks = taskMapper.selectList(taskLambdaQueryWrapper);
        tasks.forEach(task -> {
            try {
                LambdaQueryWrapper<TaskChunk> lqTaskChunk=new LambdaQueryWrapper();
                lqTaskChunk.eq(TaskChunk::getTaskId, task.getId()).in(TaskChunk::getStatus,List.of(TaskChunkStatus.READY, TaskChunkStatus.RUNNING));
                boolean exists = taskChunkMapper.exists(lqTaskChunk);
                if (!exists) {
                    task.setStatus(TaskStatus.DONE);
                    task.setCompletedAt(LocalDateTime.now());
                    LambdaQueryWrapper<TaskChunk> totalQueryWrapper=new LambdaQueryWrapper();
                    totalQueryWrapper.eq(TaskChunk::getTaskId, task.getId());
                    task.setTotalCount(taskChunkMapper.selectCount(totalQueryWrapper));

                    LambdaQueryWrapper<TaskChunk> successQueryWrapper=new LambdaQueryWrapper();
                    successQueryWrapper.eq(TaskChunk::getTaskId, task.getId()).eq(TaskChunk::getStatus, TaskChunkStatus.SUCCESS);
                    task.setTotalCount(taskChunkMapper.selectCount(totalQueryWrapper));

                    LambdaQueryWrapper<TaskChunk> failedQueryWrapper=new LambdaQueryWrapper();
                    failedQueryWrapper.eq(TaskChunk::getTaskId, task.getId()).eq(TaskChunk::getStatus, TaskChunkStatus.FAIL);
                    task.setFailedCount(taskChunkMapper.selectCount(failedQueryWrapper));

                    taskMapper.updateById(task);
                } else {
                    LambdaQueryWrapper<TaskChunk> totalQueryWrapper=new LambdaQueryWrapper();
                    totalQueryWrapper.eq(TaskChunk::getTaskId, task.getId());
                    task.setTotalCount(taskChunkMapper.selectCount(totalQueryWrapper));

                    LambdaQueryWrapper<TaskChunk> successQueryWrapper=new LambdaQueryWrapper();
                    successQueryWrapper.eq(TaskChunk::getTaskId, task.getId()).eq(TaskChunk::getStatus, TaskChunkStatus.SUCCESS);
                    task.setTotalCount(taskChunkMapper.selectCount(totalQueryWrapper));

                    LambdaQueryWrapper<TaskChunk> failedQueryWrapper=new LambdaQueryWrapper();
                    failedQueryWrapper.eq(TaskChunk::getTaskId, task.getId()).eq(TaskChunk::getStatus, TaskChunkStatus.FAIL);
                    task.setFailedCount(taskChunkMapper.selectCount(failedQueryWrapper));

                    taskMapper.updateById(task);
                    taskMapper.updateById(task);
                }
            } catch (Exception e) {
                log.error("The scan task({}) completed status failed, message: {}.", task.getId(), e.getMessage(), e);
            }
        });
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
