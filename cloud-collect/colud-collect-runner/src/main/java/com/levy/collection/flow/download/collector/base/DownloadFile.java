package com.levy.collection.flow.download.collector.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.levy.collection.flow.download.collector.payload.MinioSaveObject;

import java.util.List;

/**
 * 各个语言下载包，实现此类即可
 */
public interface DownloadFile {

    /**
     * 获取需要下载的记录总条数，用于分批下载
     * @param pageSize
     * @return
     */
    Long getTotal(Integer pageSize);

    /**
     * 拉取下载列表
     * @param pageDTO
     * @return
     */
    List<MinioSaveObject> getDownloadFileList(PageDTO pageDTO);

    /**
     * 下载文件
     * @param minioSaveObject
     */
    void download(MinioSaveObject minioSaveObject);



}
