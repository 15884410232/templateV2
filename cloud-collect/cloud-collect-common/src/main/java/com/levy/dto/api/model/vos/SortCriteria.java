package com.levy.dto.api.model.vos;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Schema(description = "排序对象")
public class SortCriteria {

    private static final String DESC = "DESC";
    private static final String ASC = "ASC";
    private static final String DEFAULT_DIRECTION = ASC;

    @Schema(description = "排序属性，需与字段相同，区分大小写，用逗号分隔，示例：createTime,name", type = "string")
    protected List<String> properties = new ArrayList<>();

    @Schema(description = "可选值[asc, desc]，大小写无关，排序方向，数量与属性相同，用逗号分隔，最后连续相同的属性可省略，示例：desc,asc或者desc", type = "string")
    protected List<String> directions = new ArrayList<>();

    public void setProperties(String properties) {
        if (properties != null) {
            this.properties = Stream.of(properties.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } else {
            this.properties = new ArrayList<>();
        }
    }

    public void setDirections(String directions) {
        if (directions != null) {
            this.directions = Stream.of(directions.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } else {
            this.directions = new ArrayList<>();
        }
    }

    public Sort toSort() {
        if (properties == null || properties.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < properties.size(); i++) {
            if (directions.size() > i) {
                Sort.Direction direction = Sort.Direction.fromOptionalString(directions.get(i)).orElse(Sort.DEFAULT_DIRECTION);
                orders.add(new Sort.Order(direction, properties.get(i)));
            } else if (!directions.isEmpty()) {
                Sort.Direction direction = Sort.Direction.fromOptionalString(directions.get(directions.size() - 1)).orElse(Sort.DEFAULT_DIRECTION);
                orders.add(new Sort.Order(direction, properties.get(i)));
            } else {
                orders.add(new Sort.Order(Sort.DEFAULT_DIRECTION, properties.get(i)));
            }
        }
        return Sort.by(orders.toArray(new Sort.Order[0]));
    }

    public <T> QueryWrapper<T> toQueryWrapper() {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (properties == null || properties.isEmpty()) {
            return wrapper;
        }
        for (int i = 0; i < properties.size(); i++) {
            if (directions.size() > i && DESC.equalsIgnoreCase(directions.get(i))) {
                wrapper.orderByDesc(properties.get(i));
            } else if (!directions.isEmpty() && DESC.equalsIgnoreCase(directions.get(directions.size() - 1))) {
                wrapper.orderByDesc(properties.get(i));
            } else {
                wrapper.orderByAsc(properties.get(i));
            }
        }
        return wrapper;
    }

}
