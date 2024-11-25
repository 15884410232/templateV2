package com.levy.dto.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
public class EnumUtil {

    public static <E extends Enum<E>> E resolve(Class<E> enumType, String name) {
        if (name == null || name.isEmpty() || enumType == null) {
            return null;
        }
        for (E e : enumType.getEnumConstants()) {
            if (name.equals(e.name())) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E resolve(Class<E> enumType, int ordinal) {
        if (enumType == null) {
            return null;
        }
        for (E e : enumType.getEnumConstants()) {
            if (e.ordinal() == ordinal) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E resolve(Class<E> enumType, String fieldName, Object fieldValue) {
        if (enumType == null) {
            return null;
        }
        try {
            Field field = enumType.getDeclaredField(fieldName);
            field.setAccessible(true);
            for (E e : enumType.getEnumConstants()) {
                if (Objects.equals(field.get(e), fieldValue)) {
                    return e;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("resolve Enum class '{}' fail, message: {}.", enumType, e.getMessage());
            return null;
        }
    }

}
