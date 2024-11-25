package com.levy.collect.service;


import com.levy.dto.api.model.dtos.collect.TaskProcessDTO;
import com.levy.dto.api.model.dtos.collect.base.CollecPageResDTO;
import com.levy.dto.collection.entity.TaskChunk;

public interface TaskProcessService {

    CollecPageResDTO<TaskChunk> findAll(TaskProcessDTO taskProcessDTO);

}

