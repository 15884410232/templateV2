package com.dtsw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Wangwang Tang
 * @since 2024-11-01
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CollectionRunnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectionRunnerApplication.class, args);
    }
}
