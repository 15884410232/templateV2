//package com.levy.collection.flow.javascript.collector;
//
//
//import com.levy.collection.flow.download.collector.base.StringChannelInboundHandler;
//import com.levy.collection.flow.javascript.collector.payload.JavaScriptStringPayLoad;
//import com.levy.collection.service.mybatis.OpenSourceSoftwareService;
//import com.levy.dto.collection.enumeration.FlowChannel;
//import com.levy.dto.collection.enumeration.FlowParameter;
//import com.levy.dto.integration.endpoint.MessageProcessor;
//import com.levy.dto.util.netty.NettyClient;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class $2ParseDetailProcessor implements MessageProcessor<String> {
//
//    private final OpenSourceSoftwareService openSourceSoftwareService;
//
//    private final StringChannelInboundHandler stringChannelInboundHandler;
//
//    @Override
//    public String from() {
//        return FlowChannel.JAVASCRIPT_COLLECTOR_PARSE_DETAIL.getChannel();
//    }
//
//    @Override
//    public Integer concurrency() {
//        return 24;
//    }
//
//    @Override
//    public Object process(String project, MessageHeaders headers){
//        String detailUrl = headers.get(FlowParameter.JS_COLLECTOR_DETAIL_URL.getName(), String.class);
//        Assert.notNull(detailUrl, "jsonUrl must not be null");
//        try {
//            URI url = UriComponentsBuilder.fromUriString(detailUrl).pathSegment(project).encode().build().toUri();
//            NettyClient.newBuilder().setPayload(new JavaScriptStringPayLoad(url.toString())).setUrl(url.toString()).setSimpleChannelInboundHandler(stringChannelInboundHandler).buildBootstrap().connectAndSend();
//        }catch (URISyntaxException e) {
//            log.error("url格式错误project:{}",project);
//        }catch (RuntimeException e){
//            log.info("JS采集失败project:{}",project);
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//}
