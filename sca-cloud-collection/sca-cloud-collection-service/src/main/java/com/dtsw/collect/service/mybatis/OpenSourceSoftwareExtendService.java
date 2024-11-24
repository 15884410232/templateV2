package com.dtsw.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtsw.collect.mapper.OpenSourceSoftwareExtendMapper;
import com.dtsw.collection.entity.OpenSourceSoftwareExtend;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenSourceSoftwareExtendService extends ServiceImpl<OpenSourceSoftwareExtendMapper, OpenSourceSoftwareExtend> {

    @Resource
    private OpenSourceSoftwareExtendMapper openSourceSoftwareExtendMapper;
}
