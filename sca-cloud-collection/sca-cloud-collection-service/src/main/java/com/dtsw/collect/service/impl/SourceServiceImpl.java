package com.dtsw.collect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dtsw.collect.mapper.SoftwareSourceMapper;
import com.dtsw.collect.mapper.TaskMapper;
import com.dtsw.collect.service.SourceService;
import com.dtsw.collection.entity.Source;
import com.dtsw.collection.entity.Task;
import com.dtsw.collection.enumeration.FlowParameter;
import com.dtsw.collection.enumeration.TaskStatus;
import com.google.common.base.CaseFormat;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import model.dtos.collect.SourceDTO;
import model.dtos.collect.base.CollecPageResDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private final SoftwareSourceMapper softwareSourceMapper;
    private final TaskMapper taskMapper;

    @Override
    public void saveOrUpdate(Source source) {
        if(source!=null && source.getId()!=null){
            softwareSourceMapper.updateById(source);
        }else{
            softwareSourceMapper.insert(source);
        }

    }

    @Override
    public Source getById(String id) {
        return getById(id, true);
    }

    public Source getById(String id, boolean nullable) {
        Source source = Optional.ofNullable(id)
                .map(softwareSourceMapper::getByIdAndNotRemove)
//                .map(softwareSourceRepository::findValidById)
                .flatMap(Function.identity())
                .orElse(null);
        if (!nullable && source == null) {
            throw new RuntimeException();
        }
        return source;
    }

    @Override
    public CollecPageResDTO<Source> getAll(SourceDTO sourceDTO) {
        CollecPageResDTO<Source> pageDTO=new CollecPageResDTO<>(sourceDTO.getPage(), sourceDTO.getSize());
        QueryWrapper<Source> queryWrapper=new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(sourceDTO.getKeyword()),"name", sourceDTO.getKeyword())
                .eq("remove", false);
        //默认按createTime排序
        if (sourceDTO.getSort().isEmpty()) {
            queryWrapper.orderByDesc("create_time");
        }else{
            sourceDTO.getSort().forEach(item->{
                if(Sort.Direction.DESC.equals(item.getDirection())){
                    queryWrapper.orderByDesc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item.getProperty()));
                }else if(Sort.Direction.ASC.equals(item.getDirection())){
                    queryWrapper.orderByAsc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item.getProperty()));
                }
            });
        }
        return softwareSourceMapper.selectPage(pageDTO, queryWrapper);
    }


    @Override
    public void start(String id, Map<String, Object> dynamicParams) {
        Source source = getById(id, false);
        dynamicParams.forEach((k, v)->{
            FlowParameter flowParameter = FlowParameter.getByName(k);
            Class<?> type = flowParameter.getType().getType();
            dynamicParams.put(k, type.cast(v));
        });

        Task task = new Task();
        task.setSourceId(source.getId());
        task.setName(source.getName());
        task.setParams(dynamicParams);
        task.setStatus(TaskStatus.READY);
        taskMapper.insert(task);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        Source source = getById(id);
        if (source != null) {
            source.setRemove(true);
            softwareSourceMapper.updateById(source);
        }
    }

}
