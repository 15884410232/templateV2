package com.dtsw.collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageChannel;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@SpringBootTest
public class FlowTest {

    @Autowired
    private ApplicationContext applicationContext;

    protected MessageChannel getChannel(String name) {
        return applicationContext.getBean(name, MessageChannel.class);
    }

}
