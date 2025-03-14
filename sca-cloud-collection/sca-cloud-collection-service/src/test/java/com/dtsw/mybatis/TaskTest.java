package com.dtsw.mybatis;

import com.dtsw.collect.mapper.TaskMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest
public class TaskTest{


    @Resource
    private TaskMapper taskMapper;

    @Test
    public void te(){
        // 给定的 date 和 time 字段
        String dateStr = "2025-01-26";
        String timeStr = "13:30";

        // 定义日期和时间的格式化器
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        // 解析字符串为 LocalDate 和 LocalTime
        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr, timeFormatter);

        // 合并为 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        System.out.println("原始时间: " + dateTime);

        // 增加三个半小时
        LocalDateTime updatedDateTime = dateTime.plus(Duration.ofHours(3).plusMinutes(30));

        System.out.println("增加后的时间: " + updatedDateTime);

        // 如果需要单独提取 Date 和 Time
        LocalDate updatedDate = updatedDateTime.toLocalDate();
        LocalTime updatedTime = updatedDateTime.toLocalTime();

        System.out.println("更新后的日期: " + updatedDate.toString());
        System.out.println("更新后的时间: " + updatedTime.format(DateTimeFormatter.ofPattern("H:mm")));
    }


}

