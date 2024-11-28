package com.levy;

import com.levy.dto.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MyTest2 {

    @Test
    public void test(){
        Long a = IdUtils.generateId("a433434343434", "0.1");
        Long a2 = IdUtils.generateId("a433434343434", "0.1");
        log.info(String.valueOf(a));
        log.info(String.valueOf(a2));

    }


}


