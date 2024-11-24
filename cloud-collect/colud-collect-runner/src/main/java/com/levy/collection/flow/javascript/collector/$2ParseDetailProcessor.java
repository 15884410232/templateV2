package com.levy.collection.flow.javascript.collector;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtsw.collection.dto.js.NpmPackage;
import com.dtsw.collection.entity.OpenSourceSoftware;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.enumeration.FlowParameter;
import com.dtsw.collection.enumeration.Language;
import com.dtsw.collection.service.mybatis.OpenSourceSoftwareExtendService;
import com.dtsw.collection.service.mybatis.OpenSourceSoftwareService;
import com.dtsw.integration.endpoint.MessageProcessor;
import com.dtsw.util.MD5Encryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class $2ParseDetailProcessor implements MessageProcessor<String> {

    private static final String JSON_PATH_SEGMENT = "json";

    private final RestTemplate restTemplate;

    private final OpenSourceSoftwareService openSourceSoftwareService;

    private final OpenSourceSoftwareExtendService openSourceSoftwareExtendService;

    @Override
    public String from() {
        return FlowChannel.JAVASCRIPT_COLLECTOR_PARSE_DETAIL.getChannel();
    }

    @Override
    public Integer concurrency() {
        return 24;
    }

    @Override
    public Object process(String project, MessageHeaders headers){
        //生成1000内的随机数
        int randomNumber = (int) (Math.random() * 1000);
        if(randomNumber==1) {
            log.info("JS采集:{}", project);
        }
        try {
            String detailUrl = headers.get(FlowParameter.JS_COLLECTOR_DETAIL_URL.getName(), String.class);
            Assert.notNull(detailUrl, "jsonUrl must not be null");
            URI url = UriComponentsBuilder.fromUriString(detailUrl).pathSegment(project).encode().build().toUri();
            JSONObject resJson = restTemplate.getForObject(url, JSONObject.class);
            saveEntity(resJson);
            return null;
        }catch (Exception e){
            log.info("JS采集失败:{}",project);
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 保存数据
     * @param resJson
     */
    public void saveEntity(JSONObject resJson) {
        // 获取 versions 字段
        JSONObject versions = resJson.getJSONObject("versions");
        // 检查 versions 是否为空
        if (versions != null) {
            // 遍历 versions 对象
            for (String versionKey : versions.keySet()) {
                JSONObject versionInfo = versions.getJSONObject(versionKey);
                Object repositoryStr = versionInfo.get("repository");
                if(repositoryStr instanceof String){
                    NpmPackage.Repository repository=new NpmPackage.Repository();
                    repository.setType("");
                    repository.setUrl(repositoryStr.toString());
                    versionInfo.put("repository", repository);
                }
                Object bugsStr = versionInfo.get("bugs");
                if(bugsStr instanceof String){
                    NpmPackage.Bugs bugs=new NpmPackage.Bugs();
                    bugs.setUrl(repositoryStr.toString());
                    versionInfo.put("bugs",   bugs);
                }
            }
        }
        Object repository = resJson.get("repository");
        if (repository != null) {
            if(repository instanceof String){
                NpmPackage.Repository repositoryObj=new NpmPackage.Repository();
                repositoryObj.setType("");
                repositoryObj.setUrl(repository.toString());
                resJson.put("repository", repositoryObj);
            }
        }
        NpmPackage npmPackage = resJson.toJavaObject(NpmPackage.class);
        Assert.notNull(npmPackage, "npmPackageList is null");
        List<OpenSourceSoftware> softwareList = new ArrayList<>();
//            List<OpenSourceSoftwareExtend> extendList = new ArrayList<>();
        npmPackage.getVersions().forEach((version, versionInfo) -> {
            OpenSourceSoftware software = new OpenSourceSoftware();
            software.setId(generateId(versionInfo.getName(), version));
            software.setName(versionInfo.getName());
            software.setVersion(version);
            if(StringUtils.isNotBlank(versionInfo.getDescription())) {
                software.setDescription(cleanInvalidUtf8(versionInfo.getDescription()));
            }
            software.setLicense(versionInfo.getLicense());
            software.setGroupId(versionInfo.getName());
            software.setHomepageUrl(versionInfo.getHomepage());
            software.setProgrammingLanguage(Language.JAVA_SCRIPT.getValue());
            Optional.ofNullable(versionInfo.getRepository()).ifPresent(item->{
                software.setRepositoryUrl(item.getUrl());
            });
            software.setDependencies(JSON.toJSONString(versionInfo.getDependencies()));
            if(versionInfo.getMaintainers()!=null){
                //将versionInfo.getMaintainers()中的name拼接起来
                software.setContributors(versionInfo.getMaintainers().stream().map(item->item.getName()).collect(Collectors.joining(",")));
            }
            software.setAuthor(JSON.toJSONString(versionInfo.getNpmUser()));
            software.setTimeCreated(versionInfo.getPublishTime());
            softwareList.add(software);
//                OpenSourceSoftwareExtend openSourceSoftwareExtend = new OpenSourceSoftwareExtend();
//                openSourceSoftwareExtend.setName(versionInfo.getName());
//                Optional.ofNullable(npmPackage.getBugs()).ifPresent(item->{
//                    openSourceSoftwareExtend.setBugTrackUrl(item.getUrl());
//                });
//                extendList.add(openSourceSoftwareExtend);
        });
        //检查数据是否有更新或者不存在
        List<OpenSourceSoftware> saveSoftwareList = softwareList.stream().filter(this::checkUpdate).collect(Collectors.toList());
        if(!saveSoftwareList.isEmpty()){
            openSourceSoftwareService.saveOrUpdateBatch(saveSoftwareList);
        }
    }


    public boolean checkUpdate(OpenSourceSoftware software ){
        LambdaQueryWrapper<OpenSourceSoftware> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OpenSourceSoftware::getId, software.getId())
                .eq(OpenSourceSoftware::getProgrammingLanguage, software.getProgrammingLanguage())
                .eq(OpenSourceSoftware::getVersion, software.getVersion())
                .eq(OpenSourceSoftware::getName, software.getName())
                .eq(software.getLicense()!=null,OpenSourceSoftware::getLicense, software.getLicense())
                .eq(software.getGroupId()!=null,OpenSourceSoftware::getGroupId, software.getGroupId())
                .eq(software.getHomepageUrl()!=null,OpenSourceSoftware::getHomepageUrl, software.getHomepageUrl())
                .eq(software.getRepositoryUrl()!=null,OpenSourceSoftware::getRepositoryUrl, software.getRepositoryUrl())
                .eq(software.getDependencies()!=null,OpenSourceSoftware::getDependencies, software.getDependencies())
                .eq(software.getContributors()!=null,OpenSourceSoftware::getContributors, software.getContributors())
                .eq(software.getAuthor()!=null,OpenSourceSoftware::getAuthor, software.getAuthor())
                .eq(software.getTimeCreated()!=null,OpenSourceSoftware::getTimeCreated, software.getTimeCreated());
        return openSourceSoftwareService.count(lambdaQueryWrapper)==0;
    }

    /**
     * 生成ID
     * @param name
     * @param version
     * @return
     */
    public String generateId(String name, String version) {
        String location = Strings.join(Stream.of("JS", name, version).filter(Objects::nonNull).iterator(), ':');
        return MD5Encryptor.encrypt(location);
    }

    /**
     * 过滤无效的UTF-8字符
     * @param input
     * @return
     */
    public static String cleanInvalidUtf8(String input) {
        // 使用正则表达式匹配无效的 UTF-8 字符
        Pattern invalidUtf8Pattern = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]");
        return invalidUtf8Pattern.matcher(input).replaceAll("");
    }
}
