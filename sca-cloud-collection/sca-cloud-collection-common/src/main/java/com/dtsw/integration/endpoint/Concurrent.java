package com.dtsw.integration.endpoint;

/**
 * 可并发执行接口
 *
 * @author Wangwang Tang
 * @since 2024-11-01
 */
public interface  Concurrent {

     default Integer concurrency(){
         return null;
     }
}
