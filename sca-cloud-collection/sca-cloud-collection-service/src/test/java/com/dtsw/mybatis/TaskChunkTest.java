//package com.dtsw.mybatis;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.dtsw.collect.service.mybatis.TaskChunkService;
//import com.dtsw.collect.service.mybatis.TaskService;
//import com.dtsw.collection.entity.Task;
//import com.dtsw.collection.entity.TaskChunk;
//import com.dtsw.collection.enumeration.TaskStatus;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@SpringBootTest
//public class TaskChunkTest {
//
//    @Resource
//    private TaskChunkService taskChunkService;
//
//    @Test
//    public void insertSource(){
////        for(int i=0;i<3;i++){
////            Task task = new Task();
////            task.setSourceId("6a8b89f620efa6f400925fd2374798f7");
////            task.setParams(Map.of("test","test"));
////            task.setStatus(TaskStatus.DONE);
////            task.setName("test");
////            task.setReason("test");
////            task.setStartedAt(LocalDateTime.now());
////
////            taskChunkService.saveOrUpdate(task);
////        }
//    }
//
//        @Test
//    void getAllTask(){
//
//        LambdaQueryWrapper<TaskChunk> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        List<TaskChunk> tasks = taskChunkService.list(lambdaQueryWrapper);
//            tasks.forEach(item ->{
//            log.info(JSON.toJSONString(item));
//        });
//    }
//}
