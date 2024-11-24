package com.levy.collection.flow.download.collector;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.dtsw.collection.constant.MessageHeaderConstants;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.download.collector.base.DownloadFile;
import com.dtsw.collection.flow.dto.MinioSaveObject;
import com.dtsw.integration.endpoint.MessageSplitter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * @author Wangwang Tang
 * @since 2024-09-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $2CreateListSplitter implements MessageSplitter<Integer>, ApplicationContextAware {

    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

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
        return FlowChannel.DOWNLOAD_PAGE_SPLITTER.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.DOWNLOAD_FILE.getChannel();
    }

    @Override
    public Integer concurrency() {
        return 1;
    }


    @Override
    public List<MinioSaveObject> split(Integer current, MessageHeaders headers) throws Exception {
        Integer pageSize = headers.get(MessageHeaderConstants.PAGESIZE, Integer.class);
        PageDTO pageDTO=new PageDTO();
        pageDTO.setCurrent(current);
        pageDTO.setSize(pageSize);
        String language = headers.get(MessageHeaderConstants.LANGUAGE, String.class);
        Assert.notNull(language, "language can not be null");
        String beanName=language+MessageHeaderConstants.DOWNLOAD;
        DownloadFile downloadFile = (DownloadFile) applicationContext.getBean(beanName);
        List<MinioSaveObject> downloadFileList = downloadFile.getDownloadFileList(pageDTO);
        return downloadFileList;
    }

}
