package com.dtsw.collection.flow.download.collector;


import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.dtsw.collection.constant.MessageHeaderConstants;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.download.collector.base.DownloadFile;
import com.dtsw.collection.flow.dto.MinioSaveObject;
import com.dtsw.integration.endpoint.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Wangwang Tang
 * @since 2024-09-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $3DownloadProcessor implements MessageProcessor<MinioSaveObject>, ApplicationContextAware {
    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
    @Override
    public String from() {
        return FlowChannel.DOWNLOAD_FILE.getChannel()+"da";
    }
    @Override
    public Integer concurrency() {
        return 24;
    }
    @Override
    public Object process(MinioSaveObject minioSaveObject, MessageHeaders headers) throws Exception {
        log.info("开始下载：{}", JSON.toJSONString(minioSaveObject));
        if(StringUtils.isBlank(minioSaveObject.getDownloadUrl())){
            return null;
        }
        String language = headers.get(MessageHeaderConstants.LANGUAGE, String.class);
        Assert.notNull(language, "language can not be null");
        String beanName=language+MessageHeaderConstants.DOWNLOAD;
        DownloadFile downloadFile = (DownloadFile) applicationContext.getBean(beanName);
        downloadFile.download(minioSaveObject);
        return null;
    }

}
