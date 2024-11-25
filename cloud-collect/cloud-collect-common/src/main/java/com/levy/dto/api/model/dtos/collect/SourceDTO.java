package com.levy.dto.api.model.dtos.collect;

import com.levy.dto.api.model.dtos.collect.base.CollecPageReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "软件源")
public class SourceDTO extends CollecPageReqDTO {

    @Schema(description = "关键字")
    private String keyword;

}
