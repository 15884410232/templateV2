package com.levy.dto.api.model.vos;


import com.levy.dto.api.model.enums.HttpCodeEnum;

import java.io.Serializable;

/**
 * 通用的结果返回类
 *
 * @param <T>
 * @author liangjun
 */
public class R<T> implements Serializable {

    private Integer code;

    private String msg;

    private T data;

    public R() {
        this.code = 200;
    }

    public R(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> R<T> errorResult(int code, String msg) {
        R<T> result = new R<>();
        return result.error(code, msg);
    }

    public static <T> R<T> okResult(int code, String msg) {
        R<T> result = new R<>();
        return result.ok(code, null, msg);
    }

    public static <T> R<T> okResult(int code, String msg, T data) {
        R<T> result = new R<>();
        return result.ok(code, data, msg);
    }

    public static <T> R<T> okResult(T data) {
        R<T> result = setHttpCodeEnum(HttpCodeEnum.SUCCESS, HttpCodeEnum.SUCCESS.getErrorMessage());
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static <T> R<T> errorResult(HttpCodeEnum enums) {
        return setHttpCodeEnum(enums, enums.getErrorMessage());
    }

    public static <T> R<T> errorResult(HttpCodeEnum enums, String errorMessage) {
        return setHttpCodeEnum(enums, errorMessage);
    }

    public static <T> R<T> errorResult(HttpCodeEnum enums, T data) {
        return setHttpCodeEnum(enums, enums.getErrorMessage(), data);
    }

    public static <T> R<T> errorResult(HttpCodeEnum enums, String errorMessage, T data) {
        return setHttpCodeEnum(enums, errorMessage, data);
    }

    public static <T> R<T> setHttpCodeEnum(HttpCodeEnum enums) {
        return okResult(enums.getCode(), enums.getErrorMessage());
    }

    private static <T> R<T> setHttpCodeEnum(HttpCodeEnum enums, String errorMessage) {
        return okResult(enums.getCode(), errorMessage);
    }

    private static <T> R<T> setHttpCodeEnum(HttpCodeEnum enums, String errorMessage, T data) {
        return okResult(enums.getCode(), errorMessage, data);
    }

    public R<T> error(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public R<T> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public R<T> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        return this;
    }

    public R<T> ok(T data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
