package com.levy.collect.service;


import com.levy.dto.api.model.dtos.collect.TaskDTO;
import com.levy.dto.api.model.dtos.collect.base.CollecPageResDTO;
import com.levy.dto.collection.entity.Task;

public interface TaskService {

    CollecPageResDTO<Task> findAll(TaskDTO taskDTO);

}
