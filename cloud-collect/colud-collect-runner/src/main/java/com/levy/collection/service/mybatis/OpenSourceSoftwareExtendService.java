package com.levy.collection.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtsw.collection.entity.OpenSourceSoftwareExtend;
import com.dtsw.collection.mapper.OpenSourceSoftwareExtendMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenSourceSoftwareExtendService extends ServiceImpl<OpenSourceSoftwareExtendMapper, OpenSourceSoftwareExtend> {

    @Resource
    private OpenSourceSoftwareExtendMapper openSourceSoftwareExtendMapper;
}
