package com.levy.collection.flow.go.collector;


import com.levy.collection.service.mybatis.OpenSourceSoftwareService;
import com.levy.dto.collection.entity.OpenSourceSoftware;
import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.integration.endpoint.MessageProcessor;
import com.levy.dto.util.MD5Encryptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Wangwang Tang
 * @since 2024-09-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $3ReadDetailFromRepoProcessor implements MessageProcessor<OpenSourceSoftware> {
    private RestTemplate restTemplate;

    private final static String attr="aria-describedby";

    private final static String attrVal="license-description";

    private final static String repoClass="UnitMeta-repo";

    private final static String herf="herf";

    private final static String repoContain="github.com";

    private OpenSourceSoftwareService openSourceSoftwareService;

    @Override
    public String from() {
        return FlowChannel.GO_READ_DETAIL.getChannel();
    }

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
    public Object process(OpenSourceSoftware sourceSoftware, MessageHeaders headers) throws Exception {
        if(sourceSoftware.getRepositoryUrl().contains(repoContain)){

        }
        return null;
    }

    public String generateId(String name, String version) {
        String location = Strings.join(Stream.of("Go", name, version).filter(Objects::nonNull).iterator(), ':');
        return MD5Encryptor.encrypt(location);
    }

}
