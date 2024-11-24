package com.levy.collection.flow.download.collector.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.dtsw.collection.constant.BucketConstants;
import com.dtsw.collection.constant.MessageHeaderConstants;
import com.dtsw.collection.dto.PythonDownLoadDto;
import com.dtsw.collection.entity.OpenSourceSoftwareExtend;
import com.dtsw.collection.enumeration.Language;
import com.dtsw.collection.flow.download.collector.base.DownloadChannelInboundHandler;
import com.dtsw.collection.flow.download.collector.base.DownloadFile;
import com.dtsw.collection.flow.dto.MinioSaveObject;
import com.dtsw.collection.mapper.OpenSourceSoftwareExtendMapper;
import com.dtsw.collection.service.store.Storage;
import com.dtsw.collection.util.HttpUtils;
import com.dtsw.util.netty.NettyClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("Python"+MessageHeaderConstants.DOWNLOAD)
public class PythonDownloadProvider implements DownloadFile {
    public static final String bucketName= BucketConstants.PythonBucketName;

    @Resource
    private Storage storage;

    @Resource
    private OpenSourceSoftwareExtendMapper openSourceSoftwareExtendMapper;

    @Override
    public Long getTotal(Integer pageSize) {
        LambdaQueryWrapper<OpenSourceSoftwareExtend> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OpenSourceSoftwareExtend::getProgrammingLanguage, Language.PYTHON.getValue());
        Long total = openSourceSoftwareExtendMapper.selectCount(lambdaQueryWrapper);
        return total;
    }

    @Override
    public List<MinioSaveObject> getDownloadFileList(PageDTO pageDTO) {
        String programmingLanguage= Language.PYTHON.getValue();
        log.info("开始查库");
        List<PythonDownLoadDto> list = openSourceSoftwareExtendMapper.getAllByProgrammingLanguage(programmingLanguage,
                pageDTO.getSize(),pageDTO.getCurrent()*pageDTO.getSize());
        log.info("结束查库");
        List<MinioSaveObject> resList=new ArrayList<>();
        list.forEach(item->{
            MinioSaveObject minioSaveObject=new MinioSaveObject();
            minioSaveObject.setBucketName(bucketName);
            if(StringUtils.isNotBlank(item.getDownloadUrl())){
                String fileName = item.getDownloadUrl().substring(item.getDownloadUrl().lastIndexOf("/") + 1);
                minioSaveObject.setObjectName(item.getName()+"/"+item.getVersion()+"/"+fileName);
            }
            minioSaveObject.setDownloadUrl(item.getDownloadUrl());
            resList.add(minioSaveObject);

        });


        return resList;
    }

    @Override
    public void download(MinioSaveObject minioSaveObject) {
        String downloadUrl = minioSaveObject.getDownloadUrl();
        Assert.notNull(downloadUrl,"downloadUrl is null");
        try {


            NettyClient.newBuilder().setUrl(minioSaveObject.getDownloadUrl()).setSimpleChannelInboundHandler(DownloadChannelInboundHandler.getInstance())
                    .buildBootstrap().connect();

            byte[] bytes = HttpUtils.get(downloadUrl, HttpResponse.BodyHandlers.ofByteArray());
            BufferedInputStream bufferedInputStream=new BufferedInputStream(new ByteArrayInputStream(bytes));
            storage.putObject(bucketName,minioSaveObject.getObjectName(),bufferedInputStream);
            bufferedInputStream.close();
            log.info("下载成功:{}",minioSaveObject.getObjectName());
        } catch (IOException e) {
            log.error("下载异常：{}"+minioSaveObject.getObjectName());
            log.error(e.getMessage());
        } catch (URISyntaxException e) {
            log.error("下载Url格式错误name：{},url:{}"+minioSaveObject.getObjectName(),minioSaveObject.getDownloadUrl());
            throw new RuntimeException(e);
        }
    }

}
