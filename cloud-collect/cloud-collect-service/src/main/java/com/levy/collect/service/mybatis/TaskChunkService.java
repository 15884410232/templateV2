package com.levy.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levy.collect.mapper.TaskChunkMapper;
import com.levy.dto.collection.entity.TaskChunk;
import org.springframework.stereotype.Service;

@Service
public class TaskChunkService extends ServiceImpl<TaskChunkMapper, TaskChunk> {
}
