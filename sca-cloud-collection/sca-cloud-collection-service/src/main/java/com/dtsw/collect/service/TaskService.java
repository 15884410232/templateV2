package com.dtsw.collect.service;

import com.dtsw.collection.entity.Task;
import model.dtos.collect.TaskDTO;
import model.dtos.collect.base.CollecPageResDTO;

public interface TaskService {

    CollecPageResDTO<Task> findAll(TaskDTO taskDTO);

}
