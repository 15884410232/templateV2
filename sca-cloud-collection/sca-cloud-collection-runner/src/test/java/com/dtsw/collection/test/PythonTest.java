package com.dtsw.collection.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.dtsw.collection.dto.Metadata;
import com.dtsw.collection.dto.PythonDownLoadDto;
import com.dtsw.collection.flow.dto.go.ModuleInfo;
import com.dtsw.collection.mapper.OpenSourceSoftwareExtendMapper;
import com.dtsw.collection.service.store.Storage;
import com.dtsw.collection.util.HttpUtils;
import com.dtsw.collection.flow.download.collector.base.DownloadChannelInboundHandler;
import com.dtsw.util.netty.NettyClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@SpringBootTest
public class PythonTest {

    @Autowired
    @Qualifier("python.collect:readProject")
    private MessageChannel splitterForm;

    @Autowired
    @Qualifier("python.collect:parseMetadata")
    private MessageChannel splitterTo;

    @Resource
    private Storage storage;

    @Resource
    private OpenSourceSoftwareExtendMapper openSourceSoftwareExtendMapper;

    @Test
    public void testSplitter() throws InterruptedException {
//        System.out.println("start test splitter");
//        Message<String> message = MessageBuilder.withPayload("1234").copyHeaders(Map.of("taskId", UUID.randomUUID(),
//                Params.SIMPLE_URL,"https://pypi.org/simple/",Params.JSON_URL,"https://pypi.org/pypi/")).build();
//        splitterForm.send(message);
//        System.out.println("end test splitter");
//        Thread.sleep(1000000);
    }

    @Test
    public void testPoll() throws InterruptedException {
        Message<String> message = MessageBuilder.withPayload("abcd").build();
        Thread.sleep(1000000);
    }

    @Test
    public void testParse() {
        OkHttpClient client = new OkHttpClient();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);
        factory.setReadTimeout((int) Duration.ofMinutes(5).toMillis());
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
        String simpleUrl = "https://pypi.org/simple/";
        Assert.notNull(simpleUrl, "simpleUrl must not be null");
        String response = restTemplate.getForObject(simpleUrl, String.class);
        Assert.notNull(response, "response must not be null");
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByTag("a");
        List<String> list = elements.stream().map(Element::html).toList();
        File file=new File("D:\\python.txt");
        //将list写入文件中
        try {
            FileWriter writer = new FileWriter(file);
            for (String s : list) {
                writer.write(s + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(list.indexOf("id8-blade"));

    }



    @Test
    public void testParse2() {
        OkHttpClient client = new OkHttpClient();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);
        factory.setReadTimeout((int) Duration.ofMinutes(5).toMillis());
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
        String JSON_PATH_SEGMENT="json";
        String project="accelerator-toolbox/";
        String jsonUrl = "https://pypi.org/pypi/";
        Assert.notNull(jsonUrl, "jsonUrl must not be null");
        URI url = UriComponentsBuilder.fromUriString(jsonUrl).pathSegment(project, JSON_PATH_SEGMENT).encode().build().toUri();
        Metadata metadata;
        try {
            metadata = restTemplate.getForObject(url, Metadata.class);

        }catch (HttpClientErrorException errorException){
            errorException.printStackTrace();
            log.error("网络请求失败：{}",url);
        }catch (DateTimeParseException dateTimeParseException){
            dateTimeParseException.printStackTrace();
            log.error("日期转换失败：{}",url);
        } catch (Exception ex){
            ex.printStackTrace();
            log.error("异常：{}",url);
        }
        int a=1;
    }


    @Test
    public void test(){
        String path="https://files.pythonhosted.org/packages/0b/fd/f08692f90ae9b97091271657de9d23730ceabdfd2c06977c54023d63a3c5/aau_ais_utilities-0.1.16.tar.gz";
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        System.out.println(fileName);
    }



    @Test
    public void te(){
        PageDTO pageDTO=new PageDTO();
        List<PythonDownLoadDto> list = openSourceSoftwareExtendMapper.getAllByProgrammingLanguage("Python",
                pageDTO.getSize(),pageDTO.getCurrent()*pageDTO.getSize());
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void test1(){
        String bucketName="python";
        String objectName="api_hour/0.8.0/api_hour";
        String path="https://pypi.python.org/pypi/api_hour";
        try {
            byte[] bytes = HttpUtils.get(path, HttpResponse.BodyHandlers.ofByteArray());
            BufferedInputStream bufferedInputStream=new BufferedInputStream(new ByteArrayInputStream(bytes));
            storage.putObject(bucketName,objectName,bufferedInputStream);
        } catch (IOException e) {
            log.info(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test12() throws URISyntaxException, InterruptedException {
        String url="https://files.pythonhosted.org/packages/fa/08/9466b723c7709192dcd93328b028500c2871462b0ba0aee809f2d9b5844d/tahrir-api-0.2.7.tar.gz";
        NettyClient.newBuilder().setUrl(url).setSimpleChannelInboundHandler(DownloadChannelInboundHandler.getInstance())
                .buildBootstrap().connectAndSend();
        System.out.println("发送完毕====================================================================================================");
        Thread.sleep(100000);
    }

    @Test
    public void test123() throws URISyntaxException, InterruptedException {
        String url= """
{"Path":"golang.org/x/text","Version":"v0.3.0","Timestamp":"2019-04-10T19:08:52.997264Z"}
                """;
        ModuleInfo moduleInfo = JSON.parseObject(url, ModuleInfo.class);
        log.info(JSON.toJSONString(moduleInfo));
    }

}