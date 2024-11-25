package com.levy.collect.service;


import com.levy.dto.api.model.dtos.collect.SourceDTO;
import com.levy.dto.api.model.dtos.collect.base.CollecPageResDTO;
import com.levy.dto.collection.entity.Source;

import java.util.Map;

public interface SourceService {
    void saveOrUpdate(Source source);

    Source getById(String id);

    CollecPageResDTO<Source> getAll(SourceDTO sourceDTO);

    void start(String id, Map<String, Object> dynamicParams);

    void deleteById(String id);
}
