package com.studia.wypozyczalnia.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.studia.wypozyczalnia.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Serwis generujący i weryfikujący tokeny JWT.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generuje token JWT dla podanego użytkownika.
     */
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
        var info = resolveUserInfo(userDetails);
        return Jwts.builder()
            .subject(info.username())
            .claim("username", info.username())
            .claim("displayName", info.displayName())
            .claim("role", info.role())
            .claim("userId", info.userId())
            .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey)
            .compact();
    }

    /**
     * Wyciąga nazwę użytkownika z tokenu.
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Wyciąga role zapisane w tokenie.
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            return List.of();
        }
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
        }
        return Collections.emptyList();
    }

    /**
     * Sprawdza poprawność tokenu względem użytkownika.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date()) && userDetails.getUsername().equals(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return parseClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private UserInfo resolveUserInfo(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal principal) {
            var user = principal.getUser();
            return new UserInfo(user.getUsername(), user.getDisplayName(),
                user.getRole() != null ? user.getRole().name() : null, user.getId());
        }
        return new UserInfo(userDetails.getUsername(), userDetails.getUsername(), null, null);
    }

    private record UserInfo(String username, String displayName, String role, Long userId) {
    }
}
