package com.levy.collection.service.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levy.collection.mapper.OpenSourceSoftwareMapper;
import com.levy.dto.collection.entity.OpenSourceSoftware;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenSourceSoftwareService extends ServiceImpl<OpenSourceSoftwareMapper, OpenSourceSoftware> {

    @Resource
    private OpenSourceSoftwareMapper openSourceSoftwareMapper;
}
