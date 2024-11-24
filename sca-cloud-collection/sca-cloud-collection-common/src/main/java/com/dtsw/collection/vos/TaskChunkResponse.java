package com.dtsw.collection.vos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dtsw.collection.enumeration.TaskChunkStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Schema(description = "任务流程信息")
public class TaskChunkResponse {


    @Schema(description = "ID")
    private String id;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "父ID")
    private String parentId;

    @Schema(description = "父ID集合")
    private List<String> parentIds;

    @Schema(description = "消息时间戳")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "区域")
    private String region;

    @Schema(description = "消息头")
    private Map<String, Object> headers;

    @Schema(description = "消息载体")
    private String payload;

    @Schema(description = "消息（序列化后）")
    private String message;

    @Schema(description = "消息管道")
    private String channel;

    @Schema(description = "消息状态")
    private TaskChunkStatus status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Schema(description = "启动时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "停止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;

    @Schema(description = "失败原因")
    private String reason;

}
