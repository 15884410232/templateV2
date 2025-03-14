package com.levy.collection.flow.download.collector;


import cn.hutool.core.lang.Assert;
import com.levy.collection.flow.download.collector.base.DownloadFile;
import com.levy.collection.flow.download.collector.payload.MinioSaveObject;
import com.levy.dto.collection.constant.MessageHeaderConstants;
import com.levy.dto.collection.enumeration.FlowChannel;
import com.levy.dto.integration.endpoint.MessageProcessor;
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
        return FlowChannel.DOWNLOAD_FILE.getChannel();
    }
    @Override
    public Integer concurrency() {
        return 24;
    }
    @Override
    public Object process(MinioSaveObject minioSaveObject, MessageHeaders headers) throws Exception {
//        log.info("开始下载：{}", JSON.toJSONString(minioSaveObject));
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
