package com.levy.dto.collection.vos;

import com.dtsw.collection.enumeration.Flow;
import com.dtsw.collection.enumeration.FlowParameterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "任务启动参数")
public class StartParamsResponse {

    @Schema(description="所属流程")
    private Flow flow;

    @Schema(description="参数名")
    private String name;

    @Schema(description="描述")
    private String description;

    @Schema(description="参数类型")
    private FlowParameterType type;

    @Schema(description="参数是否可为null")
    private boolean nullable;

    @Schema(description="示例值")
    private Object example;
}
