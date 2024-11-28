package com.levy.collection.flow.python.collector;


import com.levy.collection.flow.download.collector.base.StringPythonChannelInboundHandler;
import com.levy.collection.flow.python.collector.payload.PythonStringPayLoad;
import com.levy.collection.service.mybatis.OpenSourceSoftwareExtendService;
import com.levy.collection.service.mybatis.OpenSourceSoftwareService;
import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.collection.enumeration.FlowParameter;
import com.levy.dto.integration.endpoint.MessageProcessor;
import com.levy.dto.util.netty.NettyClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Wangwang Tang
 * @since 2024-09-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $2ParseMetadataProcessor implements MessageProcessor<String> {

    private static final String JSON_PATH_SEGMENT = "json";

    private RestTemplate restTemplate;

    private final OpenSourceSoftwareService openSourceSoftwareService;

    private final OpenSourceSoftwareExtendService openSourceSoftwareExtendService;

    @Resource
    private StringPythonChannelInboundHandler stringPythonChannelInboundHandler;


    public volatile AtomicInteger faileCount=new AtomicInteger(0);

    @PostConstruct
    private void init() {
        OkHttpClient client = new OkHttpClient();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);
        factory.setReadTimeout((int) Duration.ofMinutes(5).toMillis());
        restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
    }

    @Override
    public String from() {
        return FlowChannel.PYTHON_COLLECTOR_PARSE_METADATA.getChannel()+"da";
    }
    @Override
    public Integer concurrency() {
        return 1;
    }
    @Override
    public Object process(String project, MessageHeaders headers) throws Exception {
        faileCount.incrementAndGet();
        String jsonUrl = headers.get(FlowParameter.PYTHON_COLLECTOR_JSON_URL.getName(), String.class);
        Assert.notNull(jsonUrl, "jsonUrl must not be null");
        URI url = UriComponentsBuilder.fromUriString(jsonUrl).pathSegment(project, JSON_PATH_SEGMENT).encode().build().toUri();
        NettyClient.newBuilder().setPayload(new PythonStringPayLoad(project)).setUrl(url.toString()).setSimpleChannelInboundHandler(stringPythonChannelInboundHandler).buildBootstrap().connectAndSend();
        return null;
    }

}
