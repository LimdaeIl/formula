package com.optional.formula.common.jwt;

import com.optional.formula.common.exception.BusinessException;
import com.optional.formula.user.domain.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProvider {

    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final SecretKey secretKey;

    @Value("${spring.jwt.access-token-expiration}")
    private long accessTokenExpiation;

    @Value("${spring.jwt.refresh-token-expiration}")
    private long refreshTokenExpiation;

    private static final String PREFIX_BEARER = "Bearer ";

    public JwtProvider(@Value("${spring.jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.accessTokenKey = Keys.hmacShaKeyFor(keyBytes);
        this.refreshTokenKey = Keys.hmacShaKeyFor(keyBytes);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, UserRole userRole) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiation);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiry)
                .claim("USER_ROLE", userRole.name())
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiation);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public Long getRemainingTime(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public String getUserRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("USER_ROLE").toString();
    }

    private Claims extractClaims(String token) {
        String extractToken = token;

        if (token == null || token.isBlank()) {
            throw new BusinessException(JwtErrorCode.INVALID_BEARER_TOKEN);
        }

        if (token.startsWith("Bearer ")) {
            extractToken = token.substring("Bearer ".length());
        }

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(extractToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new BusinessException(JwtErrorCode.MALFORMED_TOKEN);
        } catch (SignatureException e) {
            throw new BusinessException(JwtErrorCode.TAMPERED_TOKEN);
        } catch (JwtException e) {
            throw new BusinessException(JwtErrorCode.INVALID_BEARER_TOKEN);
        }
    }
}

