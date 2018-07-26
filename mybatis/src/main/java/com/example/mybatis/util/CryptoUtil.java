package com.example.mybatis.util;

import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.UUID;

public class CryptoUtil {
    public static String sha1Hash(String hashStr, String salt) {
        String hashAlgorithmName = "SHA1";
        int hashIterations = 20;
        SimpleHash simpleHash = new SimpleHash(hashAlgorithmName, hashStr, salt, hashIterations);
        return simpleHash.toString();
    }
    public static String getRandomSalt() {
        return UUID.randomUUID().toString();
    }
}
