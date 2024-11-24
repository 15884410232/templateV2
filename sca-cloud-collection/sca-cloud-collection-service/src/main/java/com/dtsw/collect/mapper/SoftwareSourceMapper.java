package com.dtsw.collect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtsw.collection.entity.Source;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface SoftwareSourceMapper extends BaseMapper<Source> {


    Optional<Source> getByIdAndNotRemove(@Param("id") String id);


}
