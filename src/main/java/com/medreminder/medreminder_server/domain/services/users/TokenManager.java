package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.security.AppleTokenVerifier;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.springframework.security.authentication.BadCredentialsException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class TokenManager {

    private final JwtUtil jwtUtil;
    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final AppleTokenVerifier appleTokenVerifier;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public TokenManager(JwtUtil jwtUtil,
                        JpaRefreshTokenRepository jpaRefreshTokenRepository,
                        AppleTokenVerifier appleTokenVerifier) {
        this.jwtUtil = jwtUtil;
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
        this.appleTokenVerifier = appleTokenVerifier;
    }

    public String generateAccessToken(String userEmail, String userId) {
        return jwtUtil.generateToken(userEmail, userId);
    }

    public String generateRefreshToken() {
        return generateRandomToken();
    }

    public void storeRefreshToken(String refreshToken, UserEntity userEntity) {
        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken, userEntity);
        jpaRefreshTokenRepository.save(refreshTokenEntity);
    }

    public void revokeRefreshToken(String userId) {
        jpaRefreshTokenRepository
                .findByUserIdAndRevokedFalse(userId)
                .ifPresent(this::revokeRefreshToken);
    }

    public AuthResponse refreshToken(String token) {

        String hashToken = hashRefreshToken(token);

        RefreshTokenEntity existingRefreshToken =
                jpaRefreshTokenRepository.findByHashToken(hashToken);

        if (existingRefreshToken == null) {
            throw new BadCredentialsException("Invalid refresh token!");
        } else if (existingRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Invalid refresh token!");
        }

        existingRefreshToken.setRevoked(true);

        jpaRefreshTokenRepository.save(existingRefreshToken);

        UserEntity userEntity = existingRefreshToken.getUser();

        String accessToken = jwtUtil.generateToken(userEntity.getEmail(), userEntity.getId());

        String refreshToken = generateRandomToken();

        RefreshTokenEntity newRefreshToken = createRefreshTokenEntity(refreshToken, userEntity);

        jpaRefreshTokenRepository.save(newRefreshToken);

        return new AuthResponse(userEntity.getId(), userEntity.getEmail(), accessToken, refreshToken);
    }

    public boolean validateAppleToken(String token) {
        return appleTokenVerifier.verifyToken(token);
    }

    private RefreshTokenEntity createRefreshTokenEntity(String rawToken, UserEntity userEntity) {
        String hashToken = hashRefreshToken(rawToken);;

        final int REFRESH_TOKEN_EXPIRE_DAYS = 3650;
        Instant expiryTime = Instant.now().plus(REFRESH_TOKEN_EXPIRE_DAYS, ChronoUnit.DAYS);

        return new RefreshTokenEntity(null, hashToken, expiryTime,false, userEntity);
    }

    private void revokeRefreshToken(RefreshTokenEntity refreshTokenEntity){
        refreshTokenEntity.setRevoked(true);
        jpaRefreshTokenRepository.save(refreshTokenEntity);
    }

    private static String generateRandomToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private static String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }  catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
