package com.levy.collection.flow.python.collector;

import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.enumeration.FlowParameter;
import com.dtsw.integration.endpoint.MessageSplitter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class $1ReadProjectSplitter implements MessageSplitter<Object> {

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
        return FlowChannel.PYTHON_COLLECTOR_GATEWAY.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.PYTHON_COLLECTOR_PARSE_METADATA.getChannel();
    }

    @Override
    public List<String> split(MessageHeaders headers) throws Exception {
        String simpleUrl = headers.get(FlowParameter.PYTHON_COLLECTOR_SIMPLE_URL.getName(), String.class);
        Assert.notNull(simpleUrl, "simpleUrl must not be null");
        String response = restTemplate.getForObject(simpleUrl, String.class);
        Assert.notNull(response, "response must not be null");
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByTag("a");
        return elements.stream().map(Element::html).toList();
    }
}
