package com.levy.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levy.collect.mapper.TaskMapper;
import com.levy.dto.collection.entity.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends ServiceImpl<TaskMapper, Task> {
}
