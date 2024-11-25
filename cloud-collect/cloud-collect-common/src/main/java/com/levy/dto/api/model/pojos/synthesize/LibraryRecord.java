package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sca_cloud_library_record")
public class LibraryRecord implements Serializable {

    @TableId("library_record_id")
    private String libraryRecordId;

    @TableField("product_id")
    private String productId;

    @TableField("record_url")
    private String recordUrl;

    @TableField("software_name")
    private String softwareName;

    @TableField("community_name")
    private String communityName;

    @TableField("sourcecode_url")
    private String sourcecodeUrl;

    @TableField("country_of_origin")
    private String countryOfOrigin;

    @TableField("is_usa")
    private String isUsa;

    @TableField("public_flag")
    private Integer publicFlag;

    @TableField("encryption_flag")
    private Integer encryptionFlag;

    @TableField("non_standard_encryption_flag")
    private Integer nonStandardEncryptionFlag;

    @TableField("eccn")
    private String eccn;

    @TableField("mode")
    private String mode;

    @TableField("record_time")
    private Date recordTime;

    @TableField("record_status")
    private Integer recordStatus;

    @TableField("shared_status")
    private Integer sharedStatus;

    @TableField("invalid_reason")
    private String invalidReason;

    @TableField("record_subject")
    private String recordSubject;

    @TableField("record_person")
    private String recordPerson;

    @TableField("auditor")
    private String auditor;

    @TableField("reviewer")
    private String reviewer;

    @TableField("review_opinion")
    private String reviewOpinion;

    @TableField("audit_status")
    private Integer auditStatus;

    @TableField("audit_opinion")
    private String auditOpinion;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;


    /**
     * 备案状态
     */
    @Getter
    public enum RecordStatus {

        HAVE_BEEN_ON_RECORD(1, "已备案"),
        FOR_THE_RECORD(2, "备案中"),
        INVALID_RECORD(3, "失效");

        private final Integer code;
        private final String desc;

        RecordStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Getter
    public enum SharedStatus {

        HAVE_BEEN_ON_RECORD(1, "已备案"),
        FOR_THE_RECORD(2, "备案中"),
        INVALID_RECORD(3, "失效");

        private final Integer code;
        private final String desc;

        SharedStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Getter
    public enum AuditStatus {

        HAVE_BEEN_ON_RECORD(1, "待审核"),
        FOR_THE_RECORD(2, "待复核"),
        INVALID_RECORD(3, "已拒绝"),
        PASS_RECORD(4, "已通过");

        private final Integer code;
        private final String desc;

        AuditStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

}
