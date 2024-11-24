package com.dtsw.mybatis;

import com.dtsw.collect.mapper.TaskMapper;
import com.dtsw.collection.entity.Task;
import com.dtsw.collection.enumeration.TaskStatus;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TaskTest{


    @Resource
    private TaskMapper taskMapper;

    @Test
    public void te(){
        List<Task> allByStatus = taskMapper.getAllByStatus(TaskStatus.RUNNING);
        allByStatus.forEach(System.out::println);

    }


}

