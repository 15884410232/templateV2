package com.levy.collection.flow.download.collector.base;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.levy.collection.service.mybatis.OpenSourceSoftwareExtendService;
import com.levy.collection.service.mybatis.OpenSourceSoftwareService;
import com.levy.dto.collection.dto.Metadata;
import com.levy.dto.collection.entity.OpenSourceSoftware;
import com.levy.dto.collection.entity.OpenSourceSoftwareExtend;
import com.levy.dto.collection.enumeration.Language;
import com.levy.dto.util.netty.BasePayload;
import com.levy.dto.util.netty.BaseSimpleChannelInboundHandler;
import com.levy.dto.utils.IdUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ChannelHandler.Sharable
@Component
public class StringPythonChannelInboundHandler extends BaseSimpleChannelInboundHandler {
    public static ThreadLocal<BasePayload> minioSaveObjectThreadLocal = new ThreadLocal<>();
    @Resource
    private OpenSourceSoftwareService openSourceSoftwareService;

    @Resource
    private OpenSourceSoftwareExtendService openSourceSoftwareExtendService;


//    @Resource
//    private Storage storage;

    private Charset charset = CharsetUtil.UTF_8;

//    @Override
//    public ThreadLocal<BasePayload> getThreadLocal() {
//        return minioSaveObjectThreadLocal;
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            if (response.status().code()!=200) {
//                log.error("Non-200 status code received: " + response.status().code());
                ctx.close();
                return;
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            //将buf转成String
            String contentStr = buf.toString(charset);
            JSONObject resjson= JSON.parseObject(contentStr);
            Metadata metadata=null;
            try {
                 metadata = JSON.to(Metadata.class, resjson);
            }catch (JSONException exception){
                log.error(exception.getMessage());
            }
            saveEntity(metadata);
            if (content instanceof LastHttpContent) {
                ctx.close();
            }
        }
    }

    /**
     * 保存数据到库
     * @param metadata
     */
    public void saveEntity(Metadata metadata){

        if (metadata==null) {
            return;
        }
        List<OpenSourceSoftware> softwareList = new ArrayList<>();
        List<OpenSourceSoftwareExtend> extendList = new ArrayList<>();
        Metadata.Info info = metadata.getInfo();
        metadata.getReleases().forEach((version, release) -> {
            Long id = IdUtils.generateId(info.getName(), version);

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
            //打印当前线程名
//            log.info("Thread name: {}", Thread.currentThread().getName());
//            String s = (String)StringPythonChannelInboundHandler.minioSaveObjectThreadLocal.get();
//            log.info(s+"====="+info.getName());
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


}
