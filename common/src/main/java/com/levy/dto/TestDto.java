package com.levy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="测试数据")
@Data
public class TestDto {

    @Schema(description="姓名")
    private String name;

    @Schema(description="年龄")
    private Integer age;

    @Schema(description="地址")
    private String address;

}
