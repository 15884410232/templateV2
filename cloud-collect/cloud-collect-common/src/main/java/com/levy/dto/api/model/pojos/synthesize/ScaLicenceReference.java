package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sca_cloud_license_reference")
public class ScaLicenceReference implements Serializable {

    @TableId("licence_id")
    private String licenceId;

    @TableField("\"order\"")
    private Integer order;

    @TableField("url")
    private String url;

    @TableField("is_valid")
    private boolean isValid;

    @TableField("is_live")
    private boolean isLive;

    @TableField("is_way_back_link")
    private boolean isWayBackLink;

    @TableField("timestamp")
    private String timestamp;

}
