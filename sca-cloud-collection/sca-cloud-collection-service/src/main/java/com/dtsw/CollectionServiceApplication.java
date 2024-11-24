package com.dtsw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CollectionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectionServiceApplication.class, args);
    }
}