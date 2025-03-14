package com.dtsw.collection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtsw.collection.dto.python.PythonDownLoadDto;
import com.dtsw.collection.entity.OpenSourceSoftwareExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OpenSourceSoftwareExtendMapper extends BaseMapper<OpenSourceSoftwareExtend> {

     List<PythonDownLoadDto> getAllByProgrammingLanguage(@Param("programmingLanguage") String programmingLanguage, long size, long offset);
}
