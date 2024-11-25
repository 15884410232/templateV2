package com.levy.dto.integration.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class MessageChannelRegistry{

    @Getter
    private final Set<String> channels=new HashSet<>();

    public void addChannel(Set<String> channels) {
        this.channels.addAll(channels);
    }

    public void addChannel(String channel) {
        this.channels.add(channel);
    }

    public void addChannel(String... channel) {
        this.channels.addAll(Set.of(channel));
    }

}
