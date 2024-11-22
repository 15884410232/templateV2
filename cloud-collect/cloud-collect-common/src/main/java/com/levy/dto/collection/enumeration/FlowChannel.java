package com.levy.dto.collection.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.levy.dto.collection.enumeration.Flow.*;


/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@Getter
@RequiredArgsConstructor
public enum FlowChannel {
    JAVA_COLLECTOR_GATEWAY(JAVA_COLLECTOR, "java.collector.gateway"),
    JAVA_COLLECTOR_PARSE_INDEX(JAVA_COLLECTOR, "java.collector.parseIndex"),
    JAVA_COLLECTOR_PERSIST_INDEX(JAVA_COLLECTOR, "java.collector.persistIndex"),
    JAVA_COLLECTOR_PERSIST_SOFTWARE(JAVA_COLLECTOR, "java.collector.persistSoftware"),
    JAVA_COLLECTOR_RETRIEVE_RESOURCE(JAVA_COLLECTOR, "java.collector.retrieveResource"),
    JAVA_COLLECTOR_RESOLVE_POM(JAVA_COLLECTOR, "java.collector.resolvePom"),

    PYTHON_COLLECTOR_GATEWAY(PYTHON_COLLECTOR, "python.collect:readProject"),
    PYTHON_COLLECTOR_PARSE_METADATA(PYTHON_COLLECTOR, "python.collect:parseMetadata"),

    JAVASCRIPT_COLLECTOR_GATEWAY(JAVASCRIPT_COLLECTOR, "js.collect:gateway"),
    JAVASCRIPT_COLLECTOR_PARSE_DETAIL(JAVASCRIPT_COLLECTOR, "js.collect:parseMetadata"),
    ;

    /** 模型 */
    private final Flow flow;

    /** 消息管道 */
    private final String channel;

    // 初始化模型入口网关
    static {
        JAVA_COLLECTOR.gateway = FlowChannel.JAVA_COLLECTOR_GATEWAY;
        PYTHON_COLLECTOR.gateway=FlowChannel.PYTHON_COLLECTOR_GATEWAY;
        JAVASCRIPT_COLLECTOR.gateway=FlowChannel.JAVASCRIPT_COLLECTOR_GATEWAY;
    }
}
