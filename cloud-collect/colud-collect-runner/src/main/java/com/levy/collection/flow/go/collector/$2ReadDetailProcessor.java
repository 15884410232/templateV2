package com.levy.collection.flow.go.collector;


import com.alibaba.fastjson.JSON;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.dto.go.ModuleInfo;
import com.dtsw.integration.endpoint.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Wangwang Tang
 * @since 2024-09-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $2ReadDetailProcessor implements MessageProcessor<ModuleInfo> {

    @Override
    public String from() {
        return FlowChannel.GO_READ_DETAIL.getChannel();
    }
    @Override
    public Integer concurrency() {
        return 24;
    }
    @Override
    public Object process(ModuleInfo moduleInfo, MessageHeaders headers) throws Exception {
        log.info(JSON.toJSONString(moduleInfo));
        return null;
    }

}
