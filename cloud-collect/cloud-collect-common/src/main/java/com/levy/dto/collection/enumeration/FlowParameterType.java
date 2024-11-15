package com.levy.dto.collection.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@Getter
@RequiredArgsConstructor
public enum FlowParameterType {
    STRING(String.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    DATE(LocalDate.class),
    TIME(LocalTime.class),
    DATE_TIME(LocalDateTime.class),
    ;

    private final Class<?> type;


}
