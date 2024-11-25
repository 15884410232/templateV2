package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sca_cloud_encryption_algorithm")
public class EncryptionAlgorithm implements Serializable {

    @TableId("encryption_algorithm_id")
    private String encryptionAlgorithmId;

    @TableField("name")
    private String name;

    @TableField("full_name")
    private String fullName;

    @TableField("type")
    private String type;

    @TableField("length")
    private String length;

    @TableField("Instructions")
    private String Instructions;

    @TableField("description")
    private String description;

    @TableField("home_page")
    private String homePage;

    @TableField("permission_info")
    private String permissionInfo;

    @TableField("patent_info")
    private String patentInfo;

    @TableField("founder")
    private String founder;

    @TableField("international_standards_organization")
    private String internationalStandardsOrganization;

    @TableField("international_standard_no")
    private String internationalStandardNo;

    @TableField("standard_encryption_flag")
    private Integer standardEncryptionFlag;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;


    @Getter
    public enum Type {
        MULTI("MULTI", ""),
        OPEN_RANGE("OPEN_RANGE", ""),
        FIXED("FIXED", ""),
        CLOSED_RANGE("CLOSED_RANGE", "");
        private final String key;
        private final String desc;

        Type(String key, String desc) {
            this.key = key;
            this.desc = desc;
        }
    }

    @Getter
    public enum Status {
        MULTI("Active", "存在"),
        OPEN_RANGE("Inactive", "失效");
        private final String key;
        private final String desc;

        Status(String key, String desc) {
            this.key = key;
            this.desc = desc;
        }
    }


}
