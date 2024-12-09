package ru.itmo.marketplace.service.authentication.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.itmo.common.exception.ApiErrorCode;
import ru.itmo.common.exception.ApiException;
import ru.itmo.marketplace.service.authentication.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.secret}")
    private String secretKey;

    private final UserService userService;

    public Mono<User> extractUserAndValidate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = claims.get("user_id", Long.class);
            return userService.findById(userId).switchIfEmpty(
                    Mono.error(new ApiException(ApiErrorCode.TOKEN_INVALID))
            );
        } catch (JwtException e) {
            throw new ApiException(ApiErrorCode.TOKEN_INVALID, e);
        } catch (Exception e) {
            throw new ApiException(ApiErrorCode.SERVER_ERROR, e);
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        return createToken(claims, user.getName());
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
