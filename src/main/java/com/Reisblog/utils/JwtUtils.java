package com.Reisblog.utils;

import cn.hutool.jwt.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        System.out.println("JWT secret (first 10 chars): " + secret.substring(0, Math.min(10, secret.length())));
    }

    @Value("${jwt.expire}")
    private Long expire; // 过期时间，单位秒

    /**
     * 生成签名密钥（根据配置的 secret 字符串）
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire * 1000);
        return Jwts.builder()
                .subject(String.valueOf(userId))               // 设置用户ID作为 subject
                .issuedAt(now)                                 // 签发时间
                .expiration(expiration)                        // 过期时间
                .signWith(getSigningKey())                     // 使用密钥签名
                .compact();
    }

    /**
     * 从 token 中解析用户ID
     */
    public Long getUserIdFromToken(String token) {
        String subject = Jwts.parser()
                .verifyWith(getSigningKey())                   // 设置验证密钥
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(subject);
    }

    /**
     * 验证 token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}