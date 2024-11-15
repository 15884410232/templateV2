package com.levy.dto.collection.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {

    READY("已准备"),
    RUNNING("运行中"),
    DONE("已完成"),
    FAILED("失败"),
    ;

    private final String description;

}
