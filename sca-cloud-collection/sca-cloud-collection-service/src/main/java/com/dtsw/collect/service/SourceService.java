package com.dtsw.collect.service;

import com.dtsw.collection.entity.Source;
import model.dtos.collect.SourceDTO;
import model.dtos.collect.base.CollecPageResDTO;

import java.util.Map;

public interface SourceService {
    void saveOrUpdate(Source source);

    Source getById(String id);

    CollecPageResDTO<Source> getAll(SourceDTO sourceDTO);

    void start(String id, Map<String, Object> dynamicParams);

    void deleteById(String id);
}
