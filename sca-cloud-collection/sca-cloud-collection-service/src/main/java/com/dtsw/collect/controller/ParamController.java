package com.dtsw.collect.controller;

import com.dtsw.collection.enumeration.Flow;
import com.dtsw.collection.enumeration.FlowParameter;
import com.dtsw.collection.enumeration.Language;
import com.dtsw.collection.vos.StartParamsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.vos.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "软件源参数管理")
@RequestMapping("/v1/param")
@RestController
public class ParamController {

    /**
     * 获取所有语言
     * @return
     */
    @Operation(summary = "获取类型列表")
    @GetMapping("/language/getAll")
    public R<List<Map<String,String>>> getLanguageAll() {
        List<Map<String,String>> languageList = Arrays.asList(Language.values()).stream()
                .map(item-> Map.of("description",item.getDescription(),"id",item.getId())).collect(Collectors.toList());
        return R.okResult(languageList);
    }

    /**
     * 根据语言获取所有模型
     * @return
     */
    @Operation(summary = "根据类型获取模型")
    @GetMapping("/flow/getByLanguageId")
    public R<List<Map<String, String>>> getFlowAll(String languageId) {
        Set<Flow> reset=new HashSet<>();
        Flow[] values = Flow.values();
        for (Flow value : values) {
            for (Language language : value.getLanguages()) {
                if(language.getId().equals(languageId)){
                    reset.add(value);
                    break;
                }
            }
        }
        List<Map<String, String>> resList = reset.stream().map(item -> Map.of("id", item.getValue(),"description", item.getDescription())).collect(Collectors.toList());
        return R.okResult(resList);
    }


    @Operation(summary = "根据模型id获取启动参数")
    @GetMapping("/startParams/getByFlowId")
    public R<Object> getStartParams(String flowId) {
        Set<Flow> reset=new HashSet<>();
        List<FlowParameter> collect = Arrays.stream(FlowParameter.values()).filter(item -> item.getFlow().getValue().equals(flowId)).collect(Collectors.toList());
        List<StartParamsResponse> resList = collect.stream().map(item ->{
            StartParamsResponse startParamsResponse=new StartParamsResponse();
            startParamsResponse.setFlow(item.getFlow());
            startParamsResponse.setName(item.getName());
            startParamsResponse.setDescription(item.getDescription());
            startParamsResponse.setType(item.getType());
            startParamsResponse.setNullable(item.isNullable());
            startParamsResponse.setExample(item.getExample());
            return startParamsResponse;
        }).collect(Collectors.toList());
        return R.okResult(resList);
    }

}
