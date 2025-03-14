package com.dtsw.collection.enumeration;

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
    DOWNLOAD_RESOVLE(ALL, "下载程序"),
    GO_COLLECTOR(Go, "采集程序"),
    C_SHARP_COLLECTOR(C_SHARP, "采集程序"),
    C_C_PLUS_COLLECTOR(C_C_PLUS_PLUS, "采集程序"),
    SCANNER_RESOVLE(ALL, "扫描加密程序"),
    LICENCE_COLLECTOR(ALL, "采集程序"),
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
