package com.example.invenza.config.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtService {
    private final SecretKey secretKey;
    private final int validSeconds;
    private final JwtParser jwtParser;

    public JwtService(String secretKeyStr, int validSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        this.validSeconds = validSeconds;
    }

    public String createLoginAccessToken(MemberUserDetails user) {
        var expirationMillis = Instant.now()
                .plusSeconds(validSeconds)
                .toEpochMilli();

        var claims = Jwts.claims()
                .setSubject(user.getId());
        claims.put("name", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("phone", user.getPhone());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String jwt) throws JwtException {
        return jwtParser.parseClaimsJws(jwt).getBody();
    }
}