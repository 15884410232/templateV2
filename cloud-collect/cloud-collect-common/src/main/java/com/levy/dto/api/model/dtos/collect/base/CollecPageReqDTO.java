package com.levy.dto.api.model.dtos.collect.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * 用于请求的分页对象，适配原接口设计
 */
@Data
@Schema(description = "分页排序对象")
public class CollecPageReqDTO {

    private Sort sort=null;

    @Schema(description = "当前页：从1开始", minimum = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "页面尺寸", minimum = "1", defaultValue = "20")
    private int size = 20;

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


    public Sort getSort(){
        if(this.sort==null){
            return toSort();
        }
        return this.sort;
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



}
