package com.levy.dto.api.model.dtos.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页")
public class PageDTO {

    @Schema(description = "*页")
    private Integer pageNum = 1;

    @Schema(description = "每页*条")
    private Integer pageSize = 20;

    @Schema(description = "关键字")
    private String keyword;

}
