package com.dtsw.collect.service;

import com.dtsw.collection.entity.TaskChunk;
import model.dtos.collect.TaskProcessDTO;
import model.dtos.collect.base.CollecPageResDTO;

public interface TaskProcessService {

    CollecPageResDTO<TaskChunk> findAll(TaskProcessDTO taskProcessDTO);

}

