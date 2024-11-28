package com.levy.collection.flow.download.collector.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.levy.collection.flow.download.collector.base.DownloadChannelInboundHandler;
import com.levy.collection.flow.download.collector.base.DownloadFile;
import com.levy.collection.flow.download.collector.payload.MinioSaveObject;
import com.levy.collection.mapper.OpenSourceSoftwareExtendMapper;
//import com.levy.collection.service.store.Storage;
import com.levy.dto.collection.constant.BucketConstants;
import com.levy.dto.collection.constant.MessageHeaderConstants;
import com.levy.dto.collection.dto.PythonDownLoadDto;
import com.levy.dto.collection.entity.OpenSourceSoftwareExtend;
import com.levy.dto.collection.enumeration.Language;
import com.levy.dto.util.netty.NettyClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("Python"+ MessageHeaderConstants.DOWNLOAD)
public class PythonDownloadProvider implements DownloadFile {
    public static final String bucketName= BucketConstants.PythonBucketName;

//    @Resource
//    private Storage storage;

    @Resource
    private OpenSourceSoftwareExtendMapper openSourceSoftwareExtendMapper;

    @Resource
    private DownloadChannelInboundHandler downloadChannelInboundHandler;

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
                pageDTO.getSize(),(pageDTO.getCurrent()-1)*pageDTO.getSize());
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
            NettyClient.newBuilder().setPayload(minioSaveObject).setUrl(minioSaveObject.getDownloadUrl()).setSimpleChannelInboundHandler(downloadChannelInboundHandler)
                    .buildBootstrap()
                    .connectAndSend();
//            byte[] bytes = HttpUtils.get(downloadUrl, HttpResponse.BodyHandlers.ofByteArray());
//            BufferedInputStream bufferedInputStream=new BufferedInputStream(new ByteArrayInputStream(bytes));
//            storage.putObject(bucketName,minioSaveObject.getObjectName(),bufferedInputStream);
//            bufferedInputStream.close();
        }  catch (URISyntaxException e) {
            log.error("下载Url格式错误name：{},url:{}"+minioSaveObject.getObjectName(),minioSaveObject.getDownloadUrl());
            throw new RuntimeException(e);
        } catch (Exception exception){
            log.error("下载失败:{}",exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

}
