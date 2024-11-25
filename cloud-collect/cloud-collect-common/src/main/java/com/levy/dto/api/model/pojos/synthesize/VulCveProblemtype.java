package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sca_cloud_vul_cve_problemtype")
public class VulCveProblemtype implements Serializable {

    @TableId("vul_id")
    private String vul_id;

    @TableField("cve_problemtype")
    private String cveProblemType;


}
