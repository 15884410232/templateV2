package com.levy.dto.api.model.enums;

import lombok.Getter;

@Getter
public enum CloudModuleEnum {
    FOUNDATION("FOUNDATION", "开源基金会"),
    ENCRYPTION_ALGORITHM("ENCRYPTION_ALGORITHM", "加密算法"),
    HOSTING_PLATFORM("HOSTING_PLATFORM", "代码托管"),
    INTERNATIONAL_STANDARDS_ORGANIZATION("INTERNATIONAL_STANDARDS_ORGANIZATION", "国际组织"),
    LICENCE("LICENCE", "许可证"),
    LIBRARY_RECORD("LIBRARY_RECORD", "备案库"),
    VULNERABILITY("VULNERABILITY", "漏洞库"),
    SOURCE_SOFTWARE("SOURCE_SOFTWARE", "开源软件");

    CloudModuleEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    private final String key;
    private final String desc;

}
