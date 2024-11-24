package com.dtsw.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encryptor {

    public static String encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not find MD5 algorithm", e);
        }
    }

    // 测试方法
    public static void main(String[] args) {
        String password = "password123";
        String encryptedPassword = encrypt(password);
        System.out.println("Original Password: " + password);
        System.out.println("Encrypted Password: " + encryptedPassword);
    }
}
