package com.levy.dto.collection.enumeration;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dtsw.collection.enumeration.Language.*;

/**
 * 流程
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@Getter
@RequiredArgsConstructor
public enum Flow implements IEnum<String> {

    JAVA_COLLECTOR(JAVA, "采集程序"),
    JS_RESOLVER(JAVA_SCRIPT, "解析程序（支持Excel）"),
    PYTHON_COLLECTOR(PYTHON, "采集程序"),
    JAVASCRIPT_COLLECTOR(JAVA_SCRIPT, "采集程序"),
    ;

    /** 所属语言 */
    private final List<Language> languages;
    /** 入口通道 */
    FlowChannel gateway;
    /** 描述 */
    private final String description;

    Flow(Language language, String description) {
        this(List.of(language), description);
    }

    @Override
    public String getValue() {
        return name();
    }
}
