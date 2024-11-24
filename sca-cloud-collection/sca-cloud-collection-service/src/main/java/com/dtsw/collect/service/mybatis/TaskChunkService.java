package com.dtsw.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtsw.collect.mapper.TaskChunkMapper;
import com.dtsw.collection.entity.TaskChunk;
import org.springframework.stereotype.Service;

@Service
public class TaskChunkService extends ServiceImpl<TaskChunkMapper, TaskChunk> {
}
