package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sca_cloud_data_sub")
public class DataSub implements Serializable {

    @TableId("data_sub_id")
    private String dataSubId;

    @TableField("sub_type")
    private String subType;

    @TableField("base_line_version")
    private String baseLineVersion;

    @TableField("incremental_version")
    private String incrementalVersion;

    @TableField("payment_time_point_version")
    private Date paymentTimePointVersion;

    @TableField("create_time")
    private Date createTime;

    @TableField("latest_flag")
    private Boolean latestFlag;

    @TableField("del_flag")
    private Integer delFlag;

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
