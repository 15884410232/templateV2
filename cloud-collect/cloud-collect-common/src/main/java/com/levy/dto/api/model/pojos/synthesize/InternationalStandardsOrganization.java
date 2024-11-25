package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sca_cloud_international_standards_organization")
public class InternationalStandardsOrganization implements Serializable {

    @TableId("international_standards_organization_id")
    private String internationalStandardsOrganizationId;

    @TableField("name")
    private String name;

    @TableField("full_name")
    private String fullName;

    @TableField("description")
    private String description;

    @TableField("official_website")
    private String officialWebsite;

    @TableField("home_country")
    private String homeCountry;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private Date createTime;

    @TableField("creator")
    private String creator;

    @TableField("update_time")
    private Date updateTime;

    @TableField("updater")
    private String updater;

    @Getter
    public enum Status {
        ACTIVE("Active", "存续/在业"),
        INACTIVE("Inactive", "非活跃/停业");
        final String key;
        final String desc;

        Status(String key, String desc) {
            this.key = key;
            this.desc = desc;
        }
    }

}
