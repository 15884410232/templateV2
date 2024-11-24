package com.levy.collection.flow.java.collector.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtsw.collection.flow.java.collector.entity.JavaResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Wangwang Tang
 * @since 2024-11-10
 */
@Mapper
public interface JavaResourceMapper extends BaseMapper<JavaResource> {
    @Select("SELECT * FROM sca_cloud_java_resource WHERE full_name = #{fullName} LIMIT 1")
    JavaResource findByFullName(@Param("fullName") String fullName);
}
