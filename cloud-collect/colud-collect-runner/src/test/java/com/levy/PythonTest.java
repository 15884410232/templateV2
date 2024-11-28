package com.levy;

import com.levy.dto.collection.enumeration.Flow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

import static com.levy.dto.collection.constant.MessageHeaderConstants.TASK_ID;
import static com.levy.dto.collection.enumeration.FlowParameter.*;

@SpringBootTest
public class PythonTest extends FlowTest{

    @Test
    public void start() throws InterruptedException {
        MessageChannel channel = getChannel(Flow.PYTHON_COLLECTOR.getGateway().getChannel());
        Message<String> message = MessageBuilder.withPayload("")
                .setHeader(TASK_ID, UUID.randomUUID())
                .setHeader(PYTHON_COLLECTOR_SIMPLE_URL.getName(), "https://pypi.org/simple/")
                .setHeader(PYTHON_COLLECTOR_JSON_URL.getName(), "https://pypi.org/pypi/")
                .build();
        channel.send(message);
        // TimeUnit.HOURS.sleep(1);
    }

}
