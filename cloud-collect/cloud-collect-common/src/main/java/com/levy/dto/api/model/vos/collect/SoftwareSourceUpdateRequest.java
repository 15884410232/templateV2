package com.levy.dto.api.model.vos.collect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "修改软件源")
public class SoftwareSourceUpdateRequest {

    @Schema(description = "名称")
    private String name;

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

}
