package com.levy.dto.api.model.vos.collect;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@Schema(description = "任务信息")
public class TaskResponse {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "源ID")
    private String sourceId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "失败原因")
    private String reason;

    @Schema(description = "任务参数")
    private Map<String, Object> params;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
