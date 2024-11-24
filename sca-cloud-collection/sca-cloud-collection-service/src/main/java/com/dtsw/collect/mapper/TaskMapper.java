package com.dtsw.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtsw.collection.entity.Task;
import com.dtsw.collection.enumeration.TaskStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper extends BaseMapper<Task> {

//    List<Task> getAllByStatus(@Param("status") String status);

    List<Task> getAllByStatus(@Param("status") TaskStatus status);

}
