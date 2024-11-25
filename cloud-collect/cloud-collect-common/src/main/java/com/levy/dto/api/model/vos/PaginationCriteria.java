package com.levy.dto.api.model.vos;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Schema(description = "分页排序对象")
public class PaginationCriteria extends SortCriteria {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 200;

    @Schema(description = "当前页：从1开始", minimum = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "页面尺寸", minimum = "1", defaultValue = "20")
    private int size = 20;

    public Pageable toPageable() {
        return PageRequest.of(obtainPage() - 1, obtainSize(), toSort());
    }

    public <T> IPage<T> toPage() {
        return new Page<>(obtainPage(), obtainSize());
    }

    private int obtainPage() {
        return page < DEFAULT_PAGE ? DEFAULT_PAGE : page;
    }

    private int obtainSize() {
        return size < MIN_SIZE || size > MAX_SIZE ? DEFAULT_SIZE : size;
    }
}
