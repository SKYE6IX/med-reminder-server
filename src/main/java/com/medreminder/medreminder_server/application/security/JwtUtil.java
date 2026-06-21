package com.medreminder.medreminder_server.application.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;


@Component
public class JwtUtil {
    private final Environment env;

    @Autowired
    public JwtUtil(Environment env) {
        this.env = env;
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(env.getProperty("med.reminder.jwt.key"));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email,
                                String userId,
                                String tokenType,
                                Date expireAt){

        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .claim("user_id", userId)
                .claim("token_type",tokenType)
                .issuedAt(new Date(now))
                .expiration(expireAt)
                .signWith(getSigningKey(),Jwts.SIG.HS256)
                .compact();
    }

    public String extractClaim(String token, String claimKey) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(claimKey, String.class);
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenExpired(String token){
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, String email, String expectedType){
        try {
            final String emailFromToken = extractEmail(token);
            final String tokenType = extractClaim(token, "token_type");

            return (emailFromToken.equals(email) &&
                    (Objects.equals(tokenType, expectedType))
                    && isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}


