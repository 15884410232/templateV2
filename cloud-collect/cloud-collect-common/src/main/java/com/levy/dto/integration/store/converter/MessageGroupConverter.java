package com.levy.dto.integration.store.converter;

@FunctionalInterface
public interface MessageGroupConverter<M, E> {

    E convert(Object groupId, M message);

    static <M> MessageGroupConverter<M, M> identity() {
        return (id, t) -> t;
    }
}
