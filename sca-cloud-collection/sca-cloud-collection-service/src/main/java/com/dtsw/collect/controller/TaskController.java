package com.dtsw.collect.controller;

import com.dtsw.collect.service.TaskService;
import com.dtsw.collection.entity.Task;
import com.dtsw.collection.enumeration.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import model.dtos.collect.TaskDTO;
import model.dtos.collect.base.CollecPageResDTO;
import model.vos.P;
import model.vos.R;
import model.vos.collect.TaskResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/v1/task")
@RestController
@RequiredArgsConstructor
@Tag(name = "任务管理")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/page")
    @Operation(summary = "分页查询任务")
    public R<P<TaskResponse>> queryTaskPage(@RequestBody TaskDTO taskDTO) {
        CollecPageResDTO<Task> tasks = taskService.findAll(taskDTO);
        return R.okResult(P.of( tasks.map(this::to)));
    }

    private TaskResponse to(Task task) {
        if (task == null) {
            return null;
        }
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setSourceId(task.getSourceId());
        response.setName(task.getName());
        response.setStatus(Optional.ofNullable(task.getStatus()).map(TaskStatus::name).orElse(null));
        response.setReason(task.getReason());
        response.setParams(task.getParams());
        response.setStartedAt(task.getStartedAt());
        response.setCompletedAt(task.getCompletedAt());
        response.setCreateTime(task.getCreateTime());
        response.setUpdateTime(task.getUpdateTime());
        return response;
    }

}
