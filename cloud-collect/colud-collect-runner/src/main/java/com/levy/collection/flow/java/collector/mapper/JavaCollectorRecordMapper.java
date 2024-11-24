package com.levy.collection.flow.java.collector.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtsw.collection.flow.java.collector.entity.JavaCollectorRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@Mapper
public interface JavaCollectorRecordMapper extends BaseMapper<JavaCollectorRecord> {
    @Select("SELECT MAX(last_incremental) FROM sca_cloud_java_collector_record WHERE index_id = #{indexId}")
    Integer findMaxLastIncrementalByIndexId(@Param("indexId") String indexId);
}
