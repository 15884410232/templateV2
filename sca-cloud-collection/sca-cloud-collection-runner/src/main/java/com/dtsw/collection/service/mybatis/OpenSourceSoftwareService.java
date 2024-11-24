package com.dtsw.collection.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtsw.collection.entity.OpenSourceSoftware;
import com.dtsw.collection.mapper.OpenSourceSoftwareMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenSourceSoftwareService extends ServiceImpl<OpenSourceSoftwareMapper, OpenSourceSoftware> {

    @Resource
    private OpenSourceSoftwareMapper openSourceSoftwareMapper;
}
