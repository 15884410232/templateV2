package com.dtsw.collection.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtsw.collection.dto.js.NpmPackage;
import com.dtsw.collection.entity.OpenSourceSoftware;
import com.dtsw.collection.enumeration.FlowParameter;
import com.dtsw.collection.service.mybatis.OpenSourceSoftwareService;
import com.dtsw.util.MD5Encryptor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
public class GetDataTest {

    @Resource
    private OpenSourceSoftwareService openSourceSoftwareService;


    @Test
    public void test() throws IOException {
        String filePath = "C:\\Users\\陈其\\Documents\\WXWork\\1688854584664410\\Cache\\File\\2024-11\\names2.json";
        String simpleUrl = filePath;
        Assert.notNull(simpleUrl, "moduleListUrl must not be null");
//        InputStream inputStream = HttpUtils.get(simpleUrl);
        InputStream inputStream=new FileInputStream(simpleUrl);

        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(reader);
        List<String> list=new ArrayList<>();
        char[] buffer=new char[1024];
        list=bufferedReader.lines().collect(Collectors.toList());
        for (Object o : list.subList(1,10)) {
            log.info(o.toString());
        }
    }

    @Test
    public void getDetail(){
        OkHttpClient client = new OkHttpClient();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);
        factory.setReadTimeout((int) Duration.ofMinutes(5).toMillis());
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });

        String project = "element-ui";
        project="is-weakmap-polyfill";
        String detailUrl = "https://registry.npmmirror.com/";
        Assert.notNull(detailUrl, "jsonUrl must not be null");
        URI url = UriComponentsBuilder.fromUriString(detailUrl).pathSegment(project).encode().build().toUri();
        NpmPackage resJson = restTemplate.getForObject(url, NpmPackage.class);

    }

//    @Test
//    public void tests(){
//        OkHttpClient client = new OkHttpClient();
//        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);
//        factory.setReadTimeout((int) Duration.ofMinutes(5).toMillis());
//        RestTemplate restTemplate = new RestTemplate(factory);
//
//        restTemplate.getMessageConverters().forEach(httpMessageConverter -> {
//            if (httpMessageConverter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
//                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
//            }
//        });
//
//
//        Map<String,String> headers=new HashMap<>();
//        headers.put(FlowParameter.JS_COLLECTOR_DETAIL_URL.getName(), "https://registry.npmmirror.com/");
//        String project="is-weakmap-polyfill";
//        //生成1000内的随机数
//        int randomNumber = (int) (Math.random() * 1000);
//        if(randomNumber==1) {
//            log.info("JS采集:{}", project);
//        }
//        try {
//            String detailUrl = headers.get(FlowParameter.JS_COLLECTOR_DETAIL_URL.getName());
//            Assert.notNull(detailUrl, "jsonUrl must not be null");
//            URI url = UriComponentsBuilder.fromUriString(detailUrl).pathSegment(project).encode().build().toUri();
//            JSONObject resJson = restTemplate.getForObject(url, JSONObject.class);
//            // 获取 versions 字段
//            JSONObject versions = resJson.getJSONObject("versions");
//            // 检查 versions 是否为空
//            if (versions != null) {
//                // 遍历 versions 对象
//                for (String versionKey : versions.keySet()) {
//                    JSONObject versionInfo = versions.getJSONObject(versionKey);
//                    Object repositoryStr = versionInfo.get("repository");
//                    if(repositoryStr instanceof String){
//                        NpmPackage.Repository repository=new NpmPackage.Repository();
//                        repository.setType("");
//                        repository.setUrl(repositoryStr.toString());
//                        versionInfo.put("repository", repository);
//                    }
//                    Object bugsStr = versionInfo.get("bugs");
//                    if(bugsStr instanceof String){
//                        NpmPackage.Bugs bugs=new NpmPackage.Bugs();
//                        bugs.setUrl(repositoryStr.toString());
//                        versionInfo.put("bugs",   bugs);
//                    }
//                }
//            }
//            Object repository = resJson.get("repository");
//            if (repository != null) {
//                if(repository instanceof String){
//                    NpmPackage.Repository repositoryObj=new NpmPackage.Repository();
//                    repositoryObj.setType("");
//                    repositoryObj.setUrl(repository.toString());
//                    resJson.put("repository", repositoryObj);
//                }
//            }
//            NpmPackage npmPackage = resJson.toJavaObject(NpmPackage.class);
//            Assert.notNull(npmPackage, "npmPackageList is null");
//            List<OpenSourceSoftware> softwareList = new ArrayList<>();
////            List<OpenSourceSoftwareExtend> extendList = new ArrayList<>();
//
//            npmPackage.getVersions().forEach((version, versionInfo) -> {
//
//                OpenSourceSoftware software = new OpenSourceSoftware();
//                software.setId(generateId(versionInfo.getName(), version));
//                software.setName(versionInfo.getName());
//                software.setVersion(version);
//                if(StringUtils.isNotBlank(versionInfo.getDescription())) {
//                    software.setDescription(cleanInvalidUtf8(versionInfo.getDescription()));
//                }
//                software.setLicense(versionInfo.getLicense());
//                software.setGroupId(versionInfo.getName());
//                software.setHomepageUrl(versionInfo.getHomepage());
//                software.setProgrammingLanguage("JavaScript");
//                Optional.ofNullable(versionInfo.getRepository()).ifPresent(item->{
//                    software.setRepositoryUrl(item.getUrl());
//                });
//                software.setDependencies(JSON.toJSONString(versionInfo.getDependencies()));
//                software.setContributors(JSON.toJSONString(versionInfo.getMaintainers()));
//                software.setAuthor(JSON.toJSONString(versionInfo.getNpmUser()));
//                software.setTimeCreated(versionInfo.getPublishTime());
//                softwareList.add(software);
////                OpenSourceSoftwareExtend openSourceSoftwareExtend = new OpenSourceSoftwareExtend();
////                openSourceSoftwareExtend.setName(versionInfo.getName());
////                Optional.ofNullable(npmPackage.getBugs()).ifPresent(item->{
////                    openSourceSoftwareExtend.setBugTrackUrl(item.getUrl());
////                });
////                extendList.add(openSourceSoftwareExtend);
//            });
//            if(!softwareList.isEmpty()){
//                openSourceSoftwareService.saveOrUpdateBatch(softwareList);
//            }
////            log.info(String.valueOf(softwareList.size()));
//
//        }catch (Exception e){
//            log.info("JS采集:{}",project);
//            e.printStackTrace();
//        }
//    }



    public static String cleanInvalidUtf8(String input) {
        // 使用正则表达式匹配无效的 UTF-8 字符
        Pattern invalidUtf8Pattern = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]");
        return invalidUtf8Pattern.matcher(input).replaceAll("");
    }

    public String generateId(String name, String version) {
        String location = Strings.join(Stream.of("JS", name, version).filter(Objects::nonNull).iterator(), ':');
        return MD5Encryptor.encrypt(location);
    }

    @Test
    public void tests2(){
        String jsonUrl = "http://localhost/";
        Assert.notNull(jsonUrl, "jsonUrl must not be null");
        URI url = UriComponentsBuilder.fromUriString(jsonUrl).pathSegment("project", "JSON_PATH_SEGMENT").encode().build().toUri();
        System.out.println(url.toString());
    }

}
