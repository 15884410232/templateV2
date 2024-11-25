package com.levy.dto.api.model.enums;

import lombok.Getter;

@Getter
public enum SubTypeEnum {

    BASE_DATA("BASE_DATA", "基础数据"),
    ENCRYPTION_ALGORITHM("ENCRYPTION_ALGORITHM", "加密算法"),
    VULNERABILITY("VULNERABILITY", "漏洞库"),
    LIBRARY_RECORD("LIBRARY_RECORD", "备案库"),
    SOURCE_SOFTWARE("SOURCE_SOFTWARE", "开源软件");

    SubTypeEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    private final String key;
    private final String desc;

    public static String getEnumDesc(String key) {
        SubTypeEnum[] enums = SubTypeEnum.values();
        for (SubTypeEnum e : enums) {
            if (e.key.equals(key)) {
                return e.desc;
            }
        }
        return "";
    }

}
