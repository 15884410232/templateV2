package com.dtsw.collection.entity.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 审计字段-数据创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 审计字段-更新时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;

}
