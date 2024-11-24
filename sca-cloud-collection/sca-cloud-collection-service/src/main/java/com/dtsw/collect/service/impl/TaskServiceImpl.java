package com.dtsw.collect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dtsw.collect.mapper.TaskMapper;
import com.dtsw.collect.service.TaskService;
import com.dtsw.collection.entity.Task;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;
import model.dtos.collect.TaskDTO;
import model.dtos.collect.base.CollecPageResDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    @Override
    public CollecPageResDTO<Task> findAll(TaskDTO taskDTO) {
        CollecPageResDTO<Task> pageDTO=new CollecPageResDTO<>(taskDTO.getPage(),taskDTO.getSize());
        QueryWrapper<Task> queryWrapper=new QueryWrapper<>();
        //因为id字段为UUID所以使用apply方法显示将id转换，也可以直接xml中自定义sql
        queryWrapper.apply("id LIKE {0}", "%" +taskDTO.getKeyword()+ "%").or()
                .apply("source_id LIKE {0}", "%" +taskDTO.getKeyword()+ "%").or()
                .like(StringUtils.isNotBlank(taskDTO.getKeyword()),"name",taskDTO.getKeyword()).or()
                .like(StringUtils.isNotBlank(taskDTO.getKeyword()),"reason",taskDTO.getKeyword())
                .eq("remove", false);
        //默认按createTime排序
        if (taskDTO.getSort().isEmpty()) {
            queryWrapper.orderByDesc("create_time");
        }else{
            taskDTO.getSort().forEach(item->{
                if(Sort.Direction.DESC.equals(item.getDirection())){
                    queryWrapper.orderByDesc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item.getProperty()));
                }else if(Sort.Direction.ASC.equals(item.getDirection())){
                    queryWrapper.orderByAsc(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item.getProperty()));
                }
            });
        }
        return taskMapper.selectPage(pageDTO, queryWrapper);
    }
}
