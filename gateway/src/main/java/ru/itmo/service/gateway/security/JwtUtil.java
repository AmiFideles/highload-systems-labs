package ru.itmo.service.gateway.security;

import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.user.UserAuthDto;
import ru.itmo.service.user.client.AuthServiceClient;

@Component
public class JwtUtil {
    @Value("jwt.secret")
    private String secretKey;

    @Autowired
    private AuthServiceClient authServiceClient;

    public UserAuthDto extractUserAndValidate(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Long userId = claims.get("userId", Long.class);
        return authServiceClient.getAuthUserById(userId);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
