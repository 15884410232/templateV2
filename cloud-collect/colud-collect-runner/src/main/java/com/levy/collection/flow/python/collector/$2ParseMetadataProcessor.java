package com.levy.collection.flow.python.collector;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.levy.collection.service.mybatis.OpenSourceSoftwareExtendService;
import com.levy.collection.service.mybatis.OpenSourceSoftwareService;
import com.levy.dto.collection.dto.Metadata;
import com.levy.dto.collection.entity.OpenSourceSoftware;
import com.levy.dto.collection.entity.OpenSourceSoftwareExtend;
import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.collection.enumeration.FlowParameter;
import com.levy.dto.collection.enumeration.Language;
import com.levy.dto.integration.endpoint.MessageProcessor;
import com.levy.dto.util.MD5Encryptor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return FlowChannel.PYTHON_COLLECTOR_PARSE_METADATA.getChannel();
    }
    @Override
    public Integer concurrency() {
        return 24;
    }
    @Override
    public Object process(String project, MessageHeaders headers) throws Exception {
        //生成1000内的随机数 用于打印日志
        if((int) (Math.random() * 1000)==1) {
            log.info("Python采集:{},网络失败数量:{}", project,faileCount);
        }
        String jsonUrl = headers.get(FlowParameter.PYTHON_COLLECTOR_JSON_URL.getName(), String.class);
        Assert.notNull(jsonUrl, "jsonUrl must not be null");
        URI url = UriComponentsBuilder.fromUriString(jsonUrl).pathSegment(project, JSON_PATH_SEGMENT).encode().build().toUri();
        Metadata metadata;
        try {
            metadata = restTemplate.getForObject(url, Metadata.class);
        }catch (HttpClientErrorException errorException){
            log.error("网络请求失败：{}",url);
            //记录网络请求失败的数量
            faileCount.incrementAndGet();
            return null;
        }catch (Exception ex){
            log.error("异常：{}",url);
            ex.printStackTrace();
            return null;
        }
        Assert.notNull(metadata, "metadata is null");
        saveEntity(metadata);
        return null;
    }

    /**
     * 保存数据到库
     * @param metadata
     */
    public void saveEntity(Metadata metadata){
        List<OpenSourceSoftware> softwareList = new ArrayList<>();
        List<OpenSourceSoftwareExtend> extendList = new ArrayList<>();
        Metadata.Info info = metadata.getInfo();
        metadata.getReleases().forEach((version, release) -> {
            String id = generateId(info.getName(), version);
            if (StringUtils.isBlank(id)) {
                return;
            }
            OpenSourceSoftware software = new OpenSourceSoftware();
            software.setId(id);
            //组件编程语言
            software.setProgrammingLanguage(Language.PYTHON.getValue());
            //版本
            software.setVersion(version);
            //组件描述信息
            software.setDescription(StringUtils.isNotBlank(info.getDescription()) ? info.getDescription() : info.getSummary());
            //作者
            software.setAuthor(info.getAuthor());
            //名称
            software.setName(info.getName());
            //作者邮箱
            software.setAuthorEmail(info.getAuthorEmail());
            //主页地址
            software.setHomepageUrl(info.getHomePage());
            //许可证
            software.setLicense(info.getLicense());
            //文档地址
            software.setDocumentationUrl(info.getDocsUrl());

            //扩展数据
            OpenSourceSoftwareExtend extend = new OpenSourceSoftwareExtend();
            extend.setId(id);
            //语言
            extend.setProgrammingLanguage(Language.PYTHON.getValue());
            //License简称
            extend.setLicenseShortName(info.getLicense());
            release.forEach(item->{
                //保存源码包下载地址
                if("source".equals(item.getPythonVersion())){
                    extend.setDownloadUrl(item.getUrl());
                }
            });



            softwareList.add(software);
            extendList.add(extend);
        });
        //检查数据是否有更新或者不存在
        List<OpenSourceSoftware> saveSoftwareList = softwareList.stream().filter(this::checkUpdate).collect(Collectors.toList());
        if (!saveSoftwareList.isEmpty()) {
            openSourceSoftwareService.saveOrUpdateBatch(saveSoftwareList);
        }
        //检查数据是否有更新或者不存在
        List<OpenSourceSoftwareExtend> saveExtendList = extendList.stream().filter(this::checkUpdate).collect(Collectors.toList());
        if (!saveExtendList.isEmpty()) {
            openSourceSoftwareExtendService.saveOrUpdateBatch(saveExtendList);
        }
    }

    public boolean checkUpdate(OpenSourceSoftware software ){
        LambdaQueryWrapper<OpenSourceSoftware> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OpenSourceSoftware::getId, software.getId())
                .eq(OpenSourceSoftware::getProgrammingLanguage, software.getProgrammingLanguage())
                .eq(OpenSourceSoftware::getVersion, software.getVersion())
                .eq(OpenSourceSoftware::getName, software.getName())
                .eq(software.getDescription()!=null,OpenSourceSoftware::getDescription, software.getDescription())
                .eq(software.getAuthor()!=null,OpenSourceSoftware::getAuthor, software.getAuthor())
                .eq(software.getAuthorEmail()!=null,OpenSourceSoftware::getAuthorEmail, software.getAuthorEmail())
                .eq(software.getHomepageUrl()!=null,OpenSourceSoftware::getHomepageUrl, software.getHomepageUrl())
                .eq(software.getLicense()!=null,OpenSourceSoftware::getLicense, software.getLicense())
                .eq(software.getDocumentationUrl()!=null,OpenSourceSoftware::getDocumentationUrl, software.getDocumentationUrl());
        return openSourceSoftwareService.count(lambdaQueryWrapper)==0;
    }

    public boolean checkUpdate(OpenSourceSoftwareExtend softwareExtend ){
        LambdaQueryWrapper<OpenSourceSoftwareExtend> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OpenSourceSoftwareExtend::getId, softwareExtend.getId())
                .eq(OpenSourceSoftwareExtend::getProgrammingLanguage, softwareExtend.getProgrammingLanguage())
                .eq(softwareExtend.getLicenseShortName()!=null,OpenSourceSoftwareExtend::getLicenseShortName, softwareExtend.getLicenseShortName())
                .eq(softwareExtend.getDownloadUrl()!=null,OpenSourceSoftwareExtend::getDownloadUrl, softwareExtend.getDownloadUrl());
        return openSourceSoftwareExtendService.count(lambdaQueryWrapper)==0;
    }

    /**
     * 生成id
     * @param name
     * @param version
     * @return
     */
    public String generateId(String name, String version) {
        String location = Strings.join(Stream.of("Python", name, version).filter(Objects::nonNull).iterator(), ':');
        return MD5Encryptor.encrypt(location);
    }
}
