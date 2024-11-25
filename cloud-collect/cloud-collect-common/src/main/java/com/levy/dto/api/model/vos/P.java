package com.levy.dto.api.model.vos;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 通用分页结果类
 *
 * @author Wangwang Tang
 */
@Getter
@Setter
@NoArgsConstructor
public class P<D> {

    @Schema(description = "当前页，从 1 开始")
    private long page;

    @Schema(description = "页面尺寸")
    private long size;

    @Schema(description = "总页数")
    private long pages;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "分页内容")
    private List<D> content;

    private P(P<?> page, List<D> content) {
        this.page = page.getPage();
        this.size = page.getSize();
        this.pages = page.getPages();
        this.total = page.getTotal();
        this.content = content;
    }

    public <U> P<U> map(Function<? super D, ? extends U> converter) {
        //noinspection unchecked
        List<U> content = (List<U>) this.content.stream().map(converter).toList();
        return new P<>(this, content);
    }

    public static <D> P<D> of(long page, long size, long total, List<D> content) {
        P<D> p = new P<>();
        p.page = page;
        p.size = size;
        p.pages = total % page == 0 ? total / page : total / page + 1;
        p.total = total;
        p.content = content;
        return p;
    }

    public static <D> P<D> of(Page<D> page) {
        return of(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getContent());
    }

    public static <D> P<D> of(IPage<D> page) {
        P<D> p = new P<>();
        p.page = page.getCurrent();
        p.size = page.getSize();
        p.pages = page.getPages();
        p.total = page.getTotal();
        p.content = page.getRecords();
        return p;
    }
}
