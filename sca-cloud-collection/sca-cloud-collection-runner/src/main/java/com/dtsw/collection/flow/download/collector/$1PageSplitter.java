package com.dtsw.collection.flow.download.collector;

import cn.hutool.core.lang.Assert;
import com.dtsw.collection.constant.MessageHeaderConstants;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.download.collector.base.DownloadFile;
import com.dtsw.integration.endpoint.MessageSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class $1PageSplitter implements MessageSplitter<Object> , ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public String from() {
        return FlowChannel.DOWNLOAD_GATEWAY.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.DOWNLOAD_PAGE_SPLITTER.getChannel();
    }

    @Override
    public List<Integer> split(MessageHeaders headers) throws Exception {
        Integer pageSize = headers.get(MessageHeaderConstants.PAGESIZE, Integer.class);
        Assert.notNull(pageSize, "pageSize can not be null");
        String language = headers.get(MessageHeaderConstants.LANGUAGE, String.class);
        Assert.notNull(language, "language can not be null");
        String beanName=language+MessageHeaderConstants.DOWNLOAD;
        DownloadFile downloadFile = (DownloadFile) applicationContext.getBean(beanName);
        Long total = downloadFile.getTotal(pageSize);
        int pages = (int) Math.ceil(total / (double) pageSize);
        List<Integer> res=new ArrayList<>();
        for(int i=0;i<pages;i++){
            res.add(i+1);
        }
//        return  List.of(1,2);
        return  res;
    }


}
