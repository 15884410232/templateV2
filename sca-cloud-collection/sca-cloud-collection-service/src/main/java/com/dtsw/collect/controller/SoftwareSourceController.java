package com.dtsw.collect.controller;

import com.dtsw.collect.service.SourceService;
import com.dtsw.collection.entity.Source;
import com.dtsw.collection.enumeration.SourceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import model.dtos.collect.SourceDTO;
import model.dtos.collect.base.CollecPageResDTO;
import model.enums.HttpCodeEnum;
import model.vos.P;
import model.vos.R;
import model.vos.collect.SoftwareSourceCreateRequest;
import model.vos.collect.SoftwareSourceResponse;
import model.vos.collect.SoftwareSourceUpdateRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import utils.EnumUtil;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/v1/source")
@RestController
@RequiredArgsConstructor
@Tag(name = "软件源管理")
public class SoftwareSourceController {

    private final SourceService sourceService;

    @PostMapping
    @Operation(summary = "创建软件源")
    public R<Void> createSource(@RequestBody SoftwareSourceCreateRequest request) {
        Source source = new Source();
        source.setName(request.getName());
        source.setType(EnumUtil.resolve(SourceType.class, "id", request.getType()));
        source.setGateway(request.getGateway());
        source.setPriority(request.getPriority());
        source.setCron(request.getCron());
        source.setRemove(false);
        sourceService.saveOrUpdate(source);
        return R.okResult(null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改软件源")
    public R<Void> updateSource(@PathVariable("id") String id, @RequestBody SoftwareSourceUpdateRequest request) {
        Source source = sourceService.getById(id);
        if (source == null) {
            return R.errorResult(HttpCodeEnum.FAIL, "数据源不存在");
        }
        BeanUtils.copyProperties(request, source);
        sourceService.saveOrUpdate(source);
        return R.okResult(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除软件源")
    public R<Void> deleteSource(@PathVariable("id") String id) {
        sourceService.deleteById(id);
        return R.okResult(null);
    }

    @PostMapping("/start/{id}")
    @Operation(summary = "启动软件源")
    public R<Void> startSource(@PathVariable("id") String id, @RequestBody Map<String, Object> dynamicParams) {
        Source source = sourceService.getById(id);
        if (source == null) {
            return R.errorResult(HttpCodeEnum.FAIL, "数据源不存在");
        }
        sourceService.start(id, dynamicParams);
        return R.okResult(null);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询软件源")
    public R<P<SoftwareSourceResponse>> querySourcePage(@RequestBody SourceDTO sourceDTO) {
        CollecPageResDTO<Source> sources = sourceService.getAll(sourceDTO);
        return R.okResult(P.of(sources.map(this::to)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询软件源详情")
    public R<SoftwareSourceResponse> querySource(@PathVariable("id") String id) {
        Source source = sourceService.getById(id);
        return R.okResult(to(source));
    }

    private SoftwareSourceResponse to(Source source) {
        if (source == null) {
            return null;
        }
        SoftwareSourceResponse response = new SoftwareSourceResponse();
        response.setId(source.getId());
        response.setName(source.getName());
        response.setType(Optional.ofNullable(source.getType()).map(SourceType::getId).orElse(null));
        response.setGateway(source.getGateway());
        response.setPriority(source.getPriority());
        response.setCron(source.getCron());
        response.setCreateTime(source.getCreateTime());
        response.setUpdateTime(source.getUpdateTime());
        return response;
    }

}
