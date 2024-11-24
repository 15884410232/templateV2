package com.dtsw.collection.flow;

import com.dtsw.collection.FlowTest;
import com.dtsw.collection.enumeration.Flow;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

import static com.dtsw.collection.enumeration.FlowParameter.*;
import static com.dtsw.collection.support.BackupPreparedStatementSetter.TASK_ID;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
public class JavaCollectorTest extends FlowTest {

    @Test
    public void start() throws InterruptedException {
        MessageChannel channel = getChannel(Flow.JAVA_COLLECTOR.getGateway().getChannel());
        Message<String> message = MessageBuilder.withPayload("")
                .setHeader(TASK_ID, UUID.randomUUID())
                .setHeader(JAVA_COLLECTOR_URL.getName(), "https://repo1.maven.org/maven2/.index")
                .setHeader(JAVA_COLLECTOR_INCREMENTAL.getName(), true)
                .setHeader(JAVA_COLLECTOR_FIRST_INCREMENTAL.getName(), 849)
                .build();
        channel.send(message);
        // TimeUnit.HOURS.sleep(1);
    }

}
