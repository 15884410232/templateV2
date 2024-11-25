package com.levy.dto.api.model.vos.collect;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Schema(description = "软件源信息")
public class SoftwareSourceResponse {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "固定参数")
    private Map<String, Object> fixedParams;

    @Schema(description = "动态参数")
    private List<String> dynamicParams;

    @Schema(description = "采集模型")
    private String gateway;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "定时任务cron表达式")
    private String cron;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
