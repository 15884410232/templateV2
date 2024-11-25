package com.levy.collect.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levy.collect.mapper.OpenSourceSoftwareExtendMapper;
import com.levy.dto.collection.entity.OpenSourceSoftwareExtend;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenSourceSoftwareExtendService extends ServiceImpl<OpenSourceSoftwareExtendMapper, OpenSourceSoftwareExtend> {

    @Resource
    private OpenSourceSoftwareExtendMapper openSourceSoftwareExtendMapper;
}
