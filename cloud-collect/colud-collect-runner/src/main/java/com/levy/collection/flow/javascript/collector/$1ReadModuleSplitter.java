package com.levy.collection.flow.javascript.collector;


import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.collection.enumeration.FlowParameter;
import com.levy.dto.integration.endpoint.MessageSplitter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wangwang Tang
 * @since 2024-09-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $1ReadModuleSplitter implements MessageSplitter<Object> {

    private RestTemplate restTemplate;

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
        return FlowChannel.JAVASCRIPT_COLLECTOR_GATEWAY.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.JAVASCRIPT_COLLECTOR_PARSE_DETAIL.getChannel();
    }

    @Override
    public List<String> split(MessageHeaders headers) throws Exception {
        String simpleUrl = headers.get(FlowParameter.JS_COLLECTOR_PACKAGE_URL.getName(), String.class);
        Assert.notNull(simpleUrl, "moduleListUrl must not be null");
//        InputStream inputStream = HttpUtils.get(simpleUrl);
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(simpleUrl)));
        List<String> list=bufferedReader.lines().map(item-> item.replace("\"","")
                .replace(" ","").replace(",","")).collect(Collectors.toList());
        return list.subList(1,list.size()-1);
//        return list.subList(2,10);
    }
}
