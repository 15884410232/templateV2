package com.levy.dto.api.model.enums;


public enum HttpCodeEnum {

    // 成功段200
    SUCCESS(200, "SUCCESS"),
    // 登录段-10~-1
    NEED_LOGIN(-1, "NEED_LOGIN"),
    LOGIN_PASSWORD_ERROR(-2, "LOGIN_PASSWORD_ERROR"),
    USER_NOT_EXIST(-3, "USER_NOT_EXIST"),
    LOGIN_FAIL(401, "LOGIN_FAIL"),
    // TOKEN10~50
    TOKEN_INVALID(10, "TOKEN_INVALID"),
    TOKEN_EXPIRE(11, "TOKEN_EXPIRE"),
    TOKEN_REQUIRE(12, "TOKEN_REQUIRE"),
    LANGUAGE_REQUIRE(13, "LANGUAGE_REQUIRE"),
    HEAD_AUTH_PARAM_CHECK_ERROR(14, "HEAD_AUTH_PARAM_CHECK_ERROR"),
    TOKEN_AUTH_FAIL(15, "TOKEN_AUTH_FAIL"),
    AUTHORIZATION_NOT_EXIST(16, "AUTHORIZATION_NOT_EXIST"),
    AUTHORIZATION_PAUSE(17, "AUTHORIZATION_PAUSE"),
    AUTHORIZATION_CANCEL(18, "AUTHORIZATION_CANCEL"),
    AUTHORIZATION_EXPIRED(19, "AUTHORIZATION_EXPIRED"),
    // 参数错误 100~199
    PARAM_REQUIRE(100, "PARAM_REQUIRE"),
    PARAM_INVALID(101, "PARAM_INVALID"),
    PARAM_NULL(103, "PARAM_NULL"),
    SERVER_ERROR(104, "SERVER_ERROR"),
    SCAN_NOT_PASS(105, "SCAN_NOT_PASS"),
    // 数据错误 300~400
    FAIL(500, "FAIL"),

    // 权限限制 1000~2000
    NO_OPERATOR_AUTH(1001, "NO_OPERATOR_AUTH"),
    // 文件校验错误
    FILE_CHECK_FAIL(1002, "FILE_CHECK_FAIL"),
    FILE_FORMAT_INCORRECT(1003, "FILE_FORMAT_INCORRECT"),
    // 业务异常 2000~3000
    CONFIG_DATA_AUTO_UPDATE_FAIL(2001, "CONFIG_DATA_AUTO_UPDATE_FAIL"),
    VERSION_ALREADY_NEW(2002, "VERSION_ALREADY_NEW"),
    DATA_SUB_NOT_EXIST(2003, "DATA_SUB_NOT_EXIST"),
    SERIAL_NUMBER_NOT_EXIST(2004, "SERIAL_NUMBER_NOT_EXIST"),
    SERIAL_NUMBER_SYS_NOT_MATCH(2005, "SERIAL_NUMBER_SYS_NOT_MATCH"),
    ;

    /**
     * 返回的国际化key信息
     */
    private String key;
    private Integer code;
    private String errorMessage;

    // /**
    //  * 替换占位符中的参数
    //  *
    //  * @param params 需要替换的参数值 长度可变
    //  */
    // public String setAndToString(Object... params) {
    //     return MessageUtil.get(key, params, HeadInfoUtil.getLanguage());
    // }


    HttpCodeEnum(Integer code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
