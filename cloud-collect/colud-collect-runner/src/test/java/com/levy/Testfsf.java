package com.levy;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class Testfsf {

    @Resource
    RedisTemplate redisTemplate;


    @Test
    public void test(){
        redisTemplate.opsForValue().set("test","test");
    }

}
