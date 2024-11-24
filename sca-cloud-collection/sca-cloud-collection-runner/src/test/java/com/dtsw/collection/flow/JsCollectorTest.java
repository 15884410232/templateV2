package com.dtsw.collection.flow;

import com.dtsw.collection.FlowTest;
import com.dtsw.collection.enumeration.Flow;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.dtsw.collection.enumeration.FlowParameter.JS_COLLECTOR_DETAIL_URL;
import static com.dtsw.collection.enumeration.FlowParameter.JS_COLLECTOR_PACKAGE_URL;
import static com.dtsw.collection.support.BackupPreparedStatementSetter.TASK_ID;

public class JsCollectorTest extends FlowTest {

    @Test
    public void start() throws InterruptedException {
        MessageChannel channel = getChannel(Flow.JAVASCRIPT_COLLECTOR.getGateway().getChannel());
        Message<String> message = MessageBuilder.withPayload("")
                .setHeader(TASK_ID, UUID.randomUUID())
                .setHeader(JS_COLLECTOR_PACKAGE_URL.getName(), "C:\\Users\\陈其\\Documents\\WXWork\\1688854584664410\\Cache\\File\\2024-11\\names.json")
                .setHeader(JS_COLLECTOR_DETAIL_URL.getName(), "https://registry.npmmirror.com/")
                .build();
        channel.send(message);
         TimeUnit.HOURS.sleep(1);
    }

}
