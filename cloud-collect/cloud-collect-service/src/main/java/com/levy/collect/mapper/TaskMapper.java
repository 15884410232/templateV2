package com.levy.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.levy.dto.collection.entity.Task;
import com.levy.dto.collection.enumeration.TaskStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper extends BaseMapper<Task> {

//    List<Task> getAllByStatus(@Param("status") String status);

    List<Task> getAllByStatus(@Param("status") TaskStatus status);

}
