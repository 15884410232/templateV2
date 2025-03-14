package com.dtsw.collection.enumeration;

import com.dtsw.collection.constant.MessageHeaderConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import model.enums.HttpCodeEnum;

import static com.dtsw.collection.enumeration.FlowParameterType.*;

/**
 * 流程参数
 *
 * @author Wangwang Tang
 * @since 2024-11-05
 */
@Getter
@RequiredArgsConstructor
public enum FlowParameter {
    JAVA_COLLECTOR_URL(Flow.JAVA_COLLECTOR, "url", "仓库索引地址", STRING, "https://repo1.maven.org/maven2/.index"),
    JAVA_COLLECTOR_INCREMENTAL(Flow.JAVA_COLLECTOR, "incremental", "是否增量采集", BOOLEAN),
    JAVA_COLLECTOR_FIRST_INCREMENTAL(Flow.JAVA_COLLECTOR, "firstIncremental", "起始增量索引", INTEGER, true),
    PYTHON_COLLECTOR_SIMPLE_URL(Flow.PYTHON_COLLECTOR, "simpleUrl", "仓库地址", STRING,"https://pypi.org/simple/"),
    PYTHON_COLLECTOR_JSON_URL(Flow.PYTHON_COLLECTOR, "jsonUrl", "明细地址", STRING,"https://pypi.org/pypi/"),
    JS_COLLECTOR_PACKAGE_URL(Flow.JAVASCRIPT_COLLECTOR, "moduleListUrl", "仓库地址", STRING,"https://raw.githubusercontent.com/nice-registry/all-the-package-names/refs/heads/master/names.json"),
    JS_COLLECTOR_DETAIL_URL(Flow.JAVASCRIPT_COLLECTOR, "detailUrl", "明细地址", STRING,"https://registry.npmmirror.com/"),

    DOWNLOAD_RESOVLE_PAGE_SIZE(Flow.DOWNLOAD_RESOVLE, MessageHeaderConstants.PAGESIZE, "分页大小", INTEGER,50000),
    DOWNLOAD_RESOVLE_LANGAUGE(Flow.DOWNLOAD_RESOVLE, MessageHeaderConstants.LANGUAGE, "语言", STRING),

    GO_COLLECTOR_MOUDLE_URL(Flow.GO_COLLECTOR, "moduleListUrl", "仓库地址", STRING),
    GO_COLLECTOR_INCREMENT(Flow.GO_COLLECTOR, "increment", "是否增量跑", BOOLEAN),
    GO_COLLECTOR_MOUDLE_VERSION_LiST_URL(Flow.GO_COLLECTOR, "moduleVersionListUrl", "仓库地址", STRING),

    GO_COLLECTOR_DETAIL_PAGE_SIZE(Flow.GO_COLLECTOR, MessageHeaderConstants.PAGESIZE, "分页大小", INTEGER),
    GO_COLLECTOR_DETAIL_URL(Flow.GO_COLLECTOR, "detailUrl", "明细地址", STRING),

    C_SHARP_COLLECTOR_PACKAGE_URL(Flow.C_SHARP_COLLECTOR, "packageListUrl", "仓库分页地址", STRING,"https://api.nuget.org/v3/catalog0/index.json"),

    SCANNER_RESOVLE_PAGE_SIZE(Flow.DOWNLOAD_RESOVLE, MessageHeaderConstants.PAGESIZE, "分页大小", INTEGER),

    DART_COLLECTOR_PACKAGE_URL(Flow.GO_COLLECTOR, "packageListUrl", "仓库地址", STRING,"https://pub.dev/api/packages"),
    DART_COLLECTOR_HTML_URL(Flow.GO_COLLECTOR, "packageListUrl", "仓库地址", STRING,"https://pub.dev/packages")
//    C_C_PLUS_URL(Flow.C_C_PLUS_COLLECTOR, "libaryUrl", "仓库地址", STRING,"https://conan.io/api/search/all?topics=&licenses="),
    ;
    /** 所属流程 */
    private final Flow flow;

    /** 参数名 */
    private final String name;

    /** 描述 */
    private final String description;

    /** 参数类型 */
    private final FlowParameterType type;

    /** 参数是否可为null */
    private final boolean nullable;

    /** 示例值 */
    private final Object example;

    FlowParameter(Flow flow, String name, String description, FlowParameterType type) {
        this(flow, name, description, type, false);
    }

    FlowParameter(Flow flow, String name, String description, FlowParameterType type, boolean nullable) {
        this(flow, name, description, type, nullable, null);
    }

    FlowParameter(Flow flow, String name, String description, FlowParameterType type, Object example) {
        this(flow, name, description, type, false, example);
    }

    public static FlowParameter getByName(String name) {
        for (FlowParameter parameter : FlowParameter.values()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        throw new RuntimeException(HttpCodeEnum.PARAM_INVALID.getErrorMessage());
    }
}
