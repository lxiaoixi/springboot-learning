package com.example.mybatis.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

public class JWTUtil {

    public static String sign(String uId, String tokenId, String secret, int expireTime){
        try {
            Date date = new Date(System.currentTimeMillis() + expireTime);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withClaim("uId", uId)
                    .withJWTId(tokenId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, String> verify(String token, String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("tokenId", jwt.getId());
            hashMap.put("uId", jwt.getClaim("uId").asString());
            return hashMap;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
