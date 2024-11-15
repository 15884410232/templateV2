package com.levy;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MyTest {

    @Test
    public void test(){
        String pas="aJALDJFAEJMKJVKAE;QIOEFM;LAM;LA/AD/LA;ELAF";
        String s = Base64.encodeBase64String(pas.getBytes());
        System.out.println(s);
    }

}
