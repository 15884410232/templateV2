//package com.levy.controller;
//
//import com.levy.dto.TestDto;
//import com.levy.dto.config.TestConfig;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.annotation.Resource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Tag(name="测试类")
//@RequestMapping("test")
//@RestController
//public class TestController {
//
//    @Value("${test.test}")
//    private String test;
//
//    @Resource
//    private TestConfig testConfig;
//
//    @Operation(summary="获取测试数据")
//    @GetMapping("get")
//    public String get(){
//        return "test="+test+"------"+"test="+testConfig.getTest();
//    }
//
//}
