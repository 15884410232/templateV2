package com.levy.dto.collection.enumeration;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskChunkStatus implements IEnum<String> {

    READY("已准备"),
    RUNNING("运行中"),
    SUCCESS("成功"),
    FAIL("失败"),
    ;

    private final String description;


    @Override
    public String getValue() {
        return name();
    }
}
