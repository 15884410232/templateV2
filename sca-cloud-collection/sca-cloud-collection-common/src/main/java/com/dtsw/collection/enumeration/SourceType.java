package com.dtsw.collection.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SourceType {
    JAVA("Java", "Java"),
    C$("C#", "C#"),
    JAVA_SCRIPT("JavaScript", "JavaScript"),
    PYTHON("Python", "Python"),
    ;

    private final String id;

    private final String description;
}
