package com.levy.dto.api.model.dtos.collect.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 响应分页对象
 * @param <T>
 */
public class CollecPageResDTO<T> extends PageDTO<T> {

    public CollecPageResDTO(int page, int size) {
        super(page,size);
    }

    public CollecPageResDTO() {

    }

    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        CollecPageResDTO<U>  collecPageResDTO=new CollecPageResDTO<>();
        collecPageResDTO.setCurrent(this.getCurrent());
        collecPageResDTO.setSize(this.getSize());
        collecPageResDTO.setTotal(this.getTotal());
        return collecPageResDTO.setRecords(this.getConvertedContent(converter));
    }

    protected <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
        Assert.notNull(converter, "Function must not be null");
        Stream<T> var10000 = this.getRecords().stream();
        Objects.requireNonNull(converter);
        return (List)var10000.map(converter::apply).collect(Collectors.toList());
    }
}
