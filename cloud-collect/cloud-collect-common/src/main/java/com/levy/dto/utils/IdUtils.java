package com.levy.dto.utils;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class IdUtils {

    public static Long generateId(String name, String version) {
        String location = Strings.join(Stream.of("Python", name, version).filter(Objects::nonNull).iterator(), ':');
//        return MD5Encryptor.encrypt(location);

//        BigInteger bigInt = new BigInteger(location);
//        BigInteger mod = BigInteger.valueOf(Long.MAX_VALUE);
//        return bigInt.mod(mod).longValue();

        UUID uuid = UUID.nameUUIDFromBytes(location.getBytes());
        return (uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits());

    }
}
