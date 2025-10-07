package com.mts.aadati.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Component
@Slf4j
public class JwtService {

    @Value("${token.service.secret.key}")
    private String secretKey;
    @Value("${token.service.jwt.expiration}")
    private int jwtExpiration;
    @Value("${token.service.refresh.expiration}")
    private int refreshExpiration;


    // ===== Generate Access Token =====
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList());

        String token = generateToken(claims, userDetails.getUsername(), jwtExpiration);
        log.info("Generated Access Token for user: {} with expiration: {} ms", userDetails.getUsername(), jwtExpiration);
        return token;
    }


    // ===== Helper Method for Generate Token =====
    public String generateToken(Map<String, Object> claims, String username, int expiration) {
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();

        log.debug("Generated token for user: {} - Expiration: {}", username, new Date(System.currentTimeMillis() + expiration));
        return token;
    }

    // ===== Extract Methods =====
    public String extractUsername(String token) {
        String username = extractClaims(token, Claims::getSubject);
        log.debug("Extracted username: {} from token", username);
        return username;
    }

    public Date extractExpiration(String token) {
        Date expiration = extractClaims(token, Claims::getExpiration);
        log.debug("Extracted token expiration: {}", expiration);
        return expiration;
    }

    public List<String> extractRoles(String token) {
        List<String> roles = extractClaims(token,
                claims -> (List<String>) claims.getOrDefault("role", List.of()));
        log.debug("Extracted roles: {} from token", roles);
        return roles;
    }

    public boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.debug("Checked token expiration - Expired: {}", expired);
        return expired;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        boolean valid = extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token)
                ;
        log.debug("Checked token validity for user: {} - Valid: {}", userDetails.getUsername(), valid);
        return valid;
    }




    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired at: {}", e.getClaims().getExpiration());
            return e.getClaims();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new JwtException("JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token", e);
            throw new JwtException("JWT token is malformed", e);
        } catch (SignatureException e) {
            log.error("JWT signature does not match", e);
            throw new JwtException("JWT signature does not match", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT token", e);
            throw new JwtException("JWT token compact of handler are invalid", e);
        }
    }

    private SecretKey getSignInKey() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (IllegalArgumentException e) {
            log.warn("Secret key is not Base64 encoded, using raw bytes");
            return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
    }

}