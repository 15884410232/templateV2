package com.levy.collect.controller;


import com.levy.collect.service.TaskProcessService;
import com.levy.dto.api.model.dtos.collect.TaskProcessDTO;
import com.levy.dto.api.model.dtos.collect.base.CollecPageResDTO;
import com.levy.dto.api.model.vos.P;
import com.levy.dto.api.model.vos.R;
import com.levy.dto.collection.entity.TaskChunk;
import com.levy.dto.collection.enumeration.TaskChunkStatus;
import com.levy.dto.collection.vos.TaskChunkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/v1/task/process")
@RestController
@RequiredArgsConstructor
@Tag(name = "任务流程管理")
public class TaskChunkController {

    private final TaskProcessService taskProcessService;

    @PostMapping("/page")
    @Operation(summary = "分页查询任务流程")
    public R<P<TaskChunkResponse>> queryTaskProcessPage(@RequestBody TaskProcessDTO taskProcessDTO) {
        CollecPageResDTO<TaskChunk> taskProcesses = taskProcessService.findAll(taskProcessDTO);
        return R.okResult(P.of(taskProcesses.map(this::to)));
    }


    private TaskChunkResponse to(TaskChunk taskChunk) {
        if (taskChunk == null) {
            return null;
        }
        TaskChunkResponse response = new TaskChunkResponse();
        response.setId(taskChunk.getId());
        response.setTaskId(taskChunk.getTaskId());
        response.setPayload(taskChunk.getPayload());
        response.setStatus(taskChunk.getStatus());
        response.setReason(taskChunk.getReason());
        response.setChannel(taskChunk.getChannel());
        response.setMessage(taskChunk.getMessage());
        response.setHeaders(taskChunk.getHeaders());
        response.setParentId(Optional.ofNullable(taskChunk.getParentId()).orElse(null));
        response.setParentIds(Optional.ofNullable(taskChunk.getParentIds()).orElse(null));
        response.setRegion(taskChunk.getRegion());
        response.setTimestamp(taskChunk.getTimestamp());
        response.setCreatedTime(taskChunk.getCreatedTime());
        response.setStartTime(taskChunk.getStartTime());
        response.setStopTime(taskChunk.getStopTime());
        response.setStatus(TaskChunkStatus.valueOf(taskChunk.getStatus().name()));
        return response;
    }
}