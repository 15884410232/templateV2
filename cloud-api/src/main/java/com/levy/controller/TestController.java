package com.levy.controller;

import com.levy.dto.TestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="测试类")
@RequestMapping("test")
@RestController
public class TestController {

    @Operation(summary="获取测试数据")
    @GetMapping("get")
    public String get(@RequestBody TestDto testDto){
        return "OK";
    }

}
