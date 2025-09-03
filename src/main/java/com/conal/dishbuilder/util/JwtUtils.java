package com.conal.dishbuilder.util;

import com.conal.dishbuilder.constant.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private static final long EXPIRATION_TIME = 3 * 60 * 1000;       // 1 minute
    private static final long REFRESH_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days
    private final RedisUtils redisUtils;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor("your-super-secret-key-1111111111111111111".getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(Map<String, Object> claims, String subject, long ttlMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ttlMillis);

        return Jwts.builder()
                .setClaims(claims != null ? claims : new HashMap<>())
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = Map.of(
                "authorities", user.getAuthorities()
        );
        return generateToken(claims, user.getUsername(), EXPIRATION_TIME);
    }

    public String generateRefreshToken(UserDetails user) {
        return generateToken(null, user.getUsername(), REFRESH_TIME);
    }

    public boolean validateToken(String token) {
        try {
            getAllClaims(token); // Throws if invalid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = getAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public long getExpiry(String token) {
        Date expiration = getAllClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public LocalDateTime getRefreshExpiration() {
        Duration refreshDuration = Duration.ofDays(7); // example
        return Instant.now()
                .plus(refreshDuration)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Integer getTokenVersion(String token) {
        return getAllClaims(token).get("version", Integer.class);
    }

    public void addTokenIntoBlacklist(String token, TokenType tokenType) {
        long remainingTime = getExpiry(token) - System.currentTimeMillis();
        if (remainingTime > 0) {
            redisUtils.set(
                    redisUtils.genKey("blacklisted:", tokenType.name(), token),
                    "1",
                    remainingTime,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public boolean existsTokenInBlacklist(String token, TokenType tokenType) {
        String key = redisUtils.genKey("blacklisted:", tokenType.name(), token);
        return redisUtils.exists(key);
    }

}
