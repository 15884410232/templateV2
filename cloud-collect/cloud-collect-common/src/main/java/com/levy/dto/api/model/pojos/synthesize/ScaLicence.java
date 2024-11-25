package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sca_cloud_licence")
public class ScaLicence implements Serializable {

    @TableId("licence_id")
    private String licenceId;

    @TableField("name")
    private String name;

    @TableField("full_name")
    private String fullName;

    @TableField("content")
    private String content;

    @TableField("type")
    private String type;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("url")
    private String url;

    @TableField("official_url")
    private String officialUrl;

    @TableField("match_rule")
    private String matchRule;

    @TableField("is_osi_approved")
    private Boolean isOsiApproved;

    @TableField("is_fsf_libre")
    private Boolean isFsfLibre;

    @TableField("is_deprecated")
    private Boolean isDeprecated;

    @TableField("severity")
    private String severity;


   /* @Getter
    public enum Type {
        OPENSOURCE(1, "开源许可证"),
        COMMERCIAL(2, "开源许可证");
        private final Integer code;
        private final String desc;

        Type(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }*/

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
