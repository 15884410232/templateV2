package com.dtsw.collection.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.UUID;

/**
 * @author Wangwang Tang
 * @since 2024-11-05
 */
@SpringBootTest
public class IntegrationTest {

    @Autowired
    @Qualifier("splitter:from")
    private MessageChannel splitterForm;

    @Autowired
    @Qualifier("splitter:to")
    private MessageChannel splitterTo;

    @Test
    public void testSplitter() throws InterruptedException {
        System.out.println("start test splitter");
        Message<String> message = MessageBuilder.withPayload("1234").copyHeaders(Map.of("taskId", UUID.randomUUID())).build();
        splitterForm.send(message);
        System.out.println("end test splitter");
        Thread.sleep(1000000);
    }

    @Test
    public void testPoll() throws InterruptedException {
        Message<String> message = MessageBuilder.withPayload("abcd").build();
        Thread.sleep(1000000);
    }

}
