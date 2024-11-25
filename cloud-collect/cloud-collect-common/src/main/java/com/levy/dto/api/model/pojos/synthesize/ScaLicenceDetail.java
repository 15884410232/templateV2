package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sca_cloud_licence_detail")
public class ScaLicenceDetail implements Serializable {

    @TableId("licence_id")
    private String licenceId;

    @TableField("full_name")
    private String fullName;

    @TableField("is_osi_approved")
    private boolean isOsiApproved;

    @TableField("is_fsf_libre")
    private boolean isFsfLibre;

    @TableField("is_deprecated")
    private boolean isDeprecated;

    @TableField("license_text")
    private String licenseText;

    @TableField("standard_license_header_template")
    private String standardLicenseHeaderTemplate;

    @TableField("standard_license_template")
    private String standardLicenseTemplate;

    @TableField("license_comments")
    private String licenseComments;

    @TableField("standard_license_header")
    private String standardLicenseHeader;

    @TableField("license_text_html")
    private String licenseTextHtml;

    @TableField("standard_license_header_html")
    private String standardLicenseHeaderHtml;

}
