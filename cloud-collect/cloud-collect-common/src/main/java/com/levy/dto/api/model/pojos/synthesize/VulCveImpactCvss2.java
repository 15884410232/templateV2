package com.levy.dto.api.model.pojos.synthesize;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sca_cloud_vul_cve_impact_cvss2")
public class VulCveImpactCvss2 implements Serializable {

    @TableId("vul_id")
    private String vulId;

    @TableField("cvss_version")
    private String cvssVersion;

    @TableField("vector_string")
    private String vectorString;

    @TableField("access_vector")
    private String accessVector;

    @TableField("access_complexity")
    private String accessComplexity;

    @TableField("authentication")
    private String authentication;

    @TableField("confidentiality_impact")
    private String confidentialityImpact;

    @TableField("integrity_impact")
    private String integrityImpact;

    @TableField("availability_impact")
    private String availabilityImpact;

    @TableField("base_score")
    private String baseScore;

    @TableField("severity")
    private String severity;

    @TableField("exploitability_score")
    private String exploitabilityScore;

    @TableField("impact_score")
    private String impactScore;

    @TableField("ac_insuf_info")
    private String acInsufInfo;

    @TableField("obtain_all_privilege")
    private Boolean obtainAllPrivilege;

    @TableField("obtain_user_privilege")
    private Boolean obtainUserPrivilege;

    @TableField("obtain_other_privilege")
    private Boolean obtainOtherPrivilege;

    @TableField("user_interaction_required")
    private Boolean userInteractionRequired;

}
