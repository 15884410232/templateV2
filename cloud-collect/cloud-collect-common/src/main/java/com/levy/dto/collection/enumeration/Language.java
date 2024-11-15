package com.levy.dto.collection.enumeration;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 语言类型
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Getter
@RequiredArgsConstructor
public enum Language implements IEnum<String> {
    JAVA("Java", "Java"),
    C("C", "C"),
    C_PLUS_PLUS("C++", "C++"),
    C_SHARP("C#", "C#"),
    JAVA_SCRIPT("JavaScript", "JavaScript"),
    PYTHON("Python", "Python"),
    ;

    /** 唯一标识 */
    @JsonValue
    private final String id;

    /** 描述 */
    private final String description;


    @Override
    public String getValue() {
        return id;
    }
}
