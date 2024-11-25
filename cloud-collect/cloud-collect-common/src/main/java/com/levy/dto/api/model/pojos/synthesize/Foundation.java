package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sca_cloud_foundation")
public class Foundation implements Serializable {

    @TableId("foundation_id")
    private String foundationId;

    @TableField("name")
    private String name;

    @TableField("full_name")
    private String fullName;

    @TableField("description")
    private String description;

    @TableField("home_country")
    private String homeCountry;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @Getter
    public enum Status {
        ACTIVE("Active", "存在"),
        INACTIVE("Inactive", "失效");
        private final String key;
        private final String desc;

        Status(String key, String desc) {
            this.key = key;
            this.desc = desc;
        }
    }
}
