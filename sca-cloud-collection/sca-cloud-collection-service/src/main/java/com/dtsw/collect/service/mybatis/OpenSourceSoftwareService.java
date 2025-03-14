package com.dtsw.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtsw.collection.mapper.OpenSourceSoftwareMapper;
import com.dtsw.collection.entity.OpenSourceSoftware;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenSourceSoftwareService extends ServiceImpl<OpenSourceSoftwareMapper, OpenSourceSoftware> {

    @Resource
    private OpenSourceSoftwareMapper openSourceSoftwareMapper;
}
