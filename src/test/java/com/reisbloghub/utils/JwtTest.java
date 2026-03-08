package com.reisbloghub.utils;

import com.Reisblog.utils.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.ibatis.logging.jdbc.BaseJdbcLogger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import javax.crypto.SecretKey;

//@SpringBootTest(classes = BaseJdbcLogger.class)
public class JwtTest {

    @Test
    public void testGenerateToken() {
        // 生成安全的密钥
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        String token = Jwts.builder()
                .subject("user123")
                .claim("role", "ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1小时
                .signWith(key)
                .compact();

        System.out.println("Token: " + token);

        // 解析验证
        var claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Role: " + claims.get("role"));
    }
}
