package com.dtsw.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtsw.collect.mapper.TaskMapper;
import com.dtsw.collection.entity.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends ServiceImpl<TaskMapper, Task> {
}
