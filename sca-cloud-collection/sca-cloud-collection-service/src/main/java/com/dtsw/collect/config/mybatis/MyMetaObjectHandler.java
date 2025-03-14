package com.dtsw.collect.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
//        this.setFieldValByName("id", UUID.randomUUID(), metaObject);
        this.setFieldValByName("createTime",  LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime",  LocalDateTime.now(), metaObject);
    }
    // 更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime",  LocalDateTime.now(), metaObject);
    }
}
