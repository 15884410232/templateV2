package com.dtsw.collection.flow;

import com.dtsw.integration.endpoint.MessageSplitter;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Wangwang Tang
 * @since 2024-11-05
 */
@Component
public class TestSplitter implements MessageSplitter<String> {

    @Override
    public String from() {
        return "splitter:from";
    }

    @Override
    public String to() {
        return "splitter:to";
    }

    @Override
    public Collection<?> split(String payload) throws Exception {
        return payload == null ? null : payload.chars().mapToObj(Character::toString).toList();
    }
}
