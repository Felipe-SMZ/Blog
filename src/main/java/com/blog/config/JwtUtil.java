package com.blog.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private final long expiration = 1000 * 60 * 60; // uma hora

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email) // quem é o dono do token
                .setIssuedAt(new Date()) // data da criação
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // data expiração
                .signWith(getSigningKey()) // a chave secreta
                .compact(); // gera o token final
    }

    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) //
                .build()
                .parseClaimsJws(token) // valida a assinatura e extrai os "claims
                .getBody()
                .getSubject(); // pega o subject que no caso é o e-mail
    }
}
