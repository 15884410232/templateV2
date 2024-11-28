package com.levy.collection.flow.go.collector;


import com.alibaba.fastjson.JSON;
import com.levy.collection.flow.go.dto.ModuleInfo;
import com.levy.collection.flow.go.entity.GoModule;
import com.levy.collection.flow.go.mapper.GoModuleMaper;
import com.levy.collection.flow.go.service.GoModuleService;
import com.levy.collection.util.HttpUtils;
import com.levy.dto.collection.constant.SourceTypeConstants;
import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.collection.enumeration.FlowParameter;
import com.levy.dto.integration.endpoint.MessageSplitter;
import com.levy.dto.util.MD5Encryptor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class $1ReadMoudleSplitter implements MessageSplitter<Object> {
    private static final Integer pageSize=2000;

    private RestTemplate restTemplate;

    @Resource
    private GoModuleMaper goModuleMaper;

    @Resource
    private GoModuleService goModuleService;

    @PostConstruct
    private void init() {
        OkHttpClient client = new OkHttpClient();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);
        factory.setConnectTimeout((int) Duration.ofSeconds(60).toMillis());
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
        return FlowChannel.GO_GATEWAY.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.GO_READ_DETAIL.getChannel();
    }

    @Override
    public List<ModuleInfo> split(MessageHeaders headers) throws Exception {
        String moudleUrl = headers.get(FlowParameter.GO_COLLECTOR_MOUDLE_URL.getName(), String.class);
        String dataSourceType=headers.get(FlowParameter.GO_COLLECTOR_MOUDLE_SOURCE_TYPE.getName(), String.class);
        dataSourceType="net";
        if(SourceTypeConstants.net.equals(dataSourceType)) {
            log.info("read module from net");
            Assert.notNull(moudleUrl, "moudleUrl must not be null");
            String since = "";
            boolean getByNet = true;
            while (getByNet) {
                try {
                    String url = moudleUrl + "?since=" + since;
                    log.info(url);
                    byte[] bytes = HttpUtils.get(url, HttpResponse.BodyHandlers.ofByteArray());
                    List<ModuleInfo> list;
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))) {
                        list = bufferedReader.lines().map(item -> JSON.parseObject(item, ModuleInfo.class)).collect(Collectors.toList());
                    }
                    log.info(String.valueOf(list.size()));
                    List<GoModule> goModules = new ArrayList<>(pageSize);
                    list.stream().forEach(item -> {
                        GoModule goModule = new GoModule();
                        goModule.setId(generateId(item.getPath(), item.getVersion()));
                        goModule.setName(item.getPath());
                        goModule.setVersion(item.getVersion());
                        goModule.setRelease_time(item.getTimestamp().toLocalDateTime());
                        goModules.add(goModule);
                    });
                    if (!goModules.isEmpty()) {
                        goModuleService.saveOrUpdateBatch(goModules);
                    }
                    if (list.size() < pageSize) {
                        break;
                    }
                    since = list.get(list.size() - 1).getTimestamp().toString();
                } catch (Exception ex) {
                    log.error("read moudle error", ex);
                    break;
                }
            }
        }else{
            log.info("read moudle from local");
        }
        log.info("end");

        return null;

    }

    public String generateId(String name, String version) {
        String location = Strings.join(Stream.of("Go", name, version).filter(Objects::nonNull).iterator(), ':');
        return MD5Encryptor.encrypt(location);
    }
}
