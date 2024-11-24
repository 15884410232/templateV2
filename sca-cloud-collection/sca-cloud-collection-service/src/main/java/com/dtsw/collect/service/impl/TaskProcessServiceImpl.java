package com.dtsw.collect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dtsw.collect.mapper.TaskChunkMapper;
import com.dtsw.collect.mapper.TaskMapper;
import com.dtsw.collect.service.TaskProcessService;
import com.dtsw.collection.entity.TaskChunk;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;
import model.dtos.collect.TaskProcessDTO;
import model.dtos.collect.base.CollecPageResDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskProcessServiceImpl implements TaskProcessService {


    private final TaskMapper taskMapper;

    private final TaskChunkMapper taskChunkMapper;

    @Override
    public CollecPageResDTO<TaskChunk> findAll(TaskProcessDTO taskProcessDTO) {
        CollecPageResDTO<TaskChunk> pageDTO=new CollecPageResDTO<>(taskProcessDTO.getPage(),taskProcessDTO.getSize());
        QueryWrapper<TaskChunk> queryWrapper=new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(taskProcessDTO.getKeyword()),"id",taskProcessDTO.getKeyword()).or()
                .like(StringUtils.isNotBlank(taskProcessDTO.getKeyword()),"task_id",taskProcessDTO.getKeyword()).or()
                .like(StringUtils.isNotBlank(taskProcessDTO.getKeyword()),"parent_id",taskProcessDTO.getKeyword()).or()
                .like(StringUtils.isNotBlank(taskProcessDTO.getKeyword()),"channel",taskProcessDTO.getKeyword()).or()
                .like(StringUtils.isNotBlank(taskProcessDTO.getKeyword()),"payload",taskProcessDTO.getKeyword()).or()
                .like(StringUtils.isNotBlank(taskProcessDTO.getKeyword()),"reason",taskProcessDTO.getKeyword())
                .eq("remove", false);
        //默认按createTime排序
        if (taskProcessDTO.getSort().isEmpty()) {
            queryWrapper.orderByDesc("create_time");
        }else{
            taskProcessDTO.getSort().forEach(item->{
                if(Sort.Direction.DESC.equals(item.getDirection())){
                    queryWrapper.orderByDesc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item.getProperty()));
                }else if(Sort.Direction.ASC.equals(item.getDirection())){
                    queryWrapper.orderByAsc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item.getProperty()));
                }
            });
        }
        return taskChunkMapper.selectPage(pageDTO, queryWrapper);
    }
}
