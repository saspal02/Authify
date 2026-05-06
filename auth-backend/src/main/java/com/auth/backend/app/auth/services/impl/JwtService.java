package com.auth.backend.app.auth.services.impl;

import com.auth.backend.app.auth.entities.Role;
import com.auth.backend.app.auth.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Getter
@Setter
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-ttl}") long accessTtlSeconds,
                      @Value("${jwt.refresh-ttl}") long refreshTtlSeconds,
                      @Value("${jwt.issuer}") String issuer) {
        if (secret == null || secret.length() < 64) {
            throw new IllegalArgumentException("Invalid JWT secret");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    // generate access token
    public String generateAccessToken(User user) {
       Instant now = Instant.now();
       List<String> roles = user.getRoles() == null ? List.of() :
               user.getRoles().stream().map(Role::getName).toList();
       return Jwts.builder()
               .id(UUID.randomUUID().toString())
               .subject(user.getId().toString())
               .issuer(issuer)
               .issuedAt(Date.from(now))
               .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
               .claims(Map.of(
                          "email", user.getEmail(),
                          "roles", roles,
                          "type", "access"

               ))
               .signWith(key, Jwts.SIG.HS512)
               .compact();

    }
    // generate refresh token
    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("type", "refresh")
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

   // parse and validate token
   public Jws<Claims> parse(String token) {
       return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
   }

   public boolean isAccessToken(String token) {
        Claims c = parse(token).getPayload();
        return "access".equals(c.get("type", String.class));
   }

   public boolean isRefreshToken(String token) {
        Claims c = parse(token).getPayload();
        return "refresh".equals(c.get("type", String.class));
   }

   public UUID getUserId(String token) {
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getSubject());
   }

   public String getJti(String token) {
        return parse(token).getPayload().getId();
   }
}
