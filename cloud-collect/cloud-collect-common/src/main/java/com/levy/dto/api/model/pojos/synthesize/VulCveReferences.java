package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sca_cloud_vul_cve_references")
public class VulCveReferences implements Serializable {

    @TableId("vul_id")
    private String vulId;

    @TableField("vul_ref_url")
    private String vulRefUrl;

    @TableField("vul_ref_name")
    private String vulRefName;

    @TableField("vul_ref_source")
    private String vulRefSource;

    @TableField("tags")
    private String tags;

}
