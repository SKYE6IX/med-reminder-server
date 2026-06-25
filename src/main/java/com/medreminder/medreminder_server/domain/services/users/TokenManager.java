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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        long now = System.currentTimeMillis();

        return jwtUtil.generateToken(userEmail, userId,
                "access", new Date(now + 1000 * 60 * 30) );
    }

    public String generateRefreshToken() {
        return generateRandomToken();
    }

    public void storeRefreshToken(String refreshToken, UserEntity userEntity) {
        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken, userEntity);
        jpaRefreshTokenRepository.save(refreshTokenEntity);
    }


    public void revokeRefreshToken(String userId) {
        List <RefreshTokenEntity> refreshTokens = jpaRefreshTokenRepository
                .findAllByUserIdAndRevokedFalse(userId);

        if(!refreshTokens.isEmpty()){
            refreshTokens.forEach(refreshTokenEntity -> {
                refreshTokenEntity.setRevoked(true);
                refreshTokenEntity.updateExpiredAt(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            });
            jpaRefreshTokenRepository.saveAll(refreshTokens);
        }
    }

    public AuthResponse refreshToken(String token) {
        String hashToken = hashToken(token);

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

        long now = System.currentTimeMillis();
        String accessToken = jwtUtil.generateToken(userEntity.getEmail(),
                userEntity.getId(),
                "access", new Date(now + 1000 * 60 * 30) );

        String refreshToken = generateRandomToken();

        RefreshTokenEntity newRefreshToken = createRefreshTokenEntity(refreshToken, userEntity);

        jpaRefreshTokenRepository.save(newRefreshToken);

        return new AuthResponse(userEntity.getId(), userEntity.getEmail(), accessToken, refreshToken);
    }

    public boolean validateAppleToken(String token) {
        return appleTokenVerifier.verifyToken(token);
    }

    public int generatePasswordResetRawToken() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    public String getPasswordResetHashToken(String token) {
        return hashToken(token);
    }

    public boolean validatePasswordResetToken(String rawToken, String hashToken) {
      return hashToken(rawToken).equals(hashToken);
    }

//    HELPER METHODS.
    private RefreshTokenEntity createRefreshTokenEntity(String rawToken, UserEntity userEntity) {
        String hashToken = hashToken(rawToken);;
        final int REFRESH_TOKEN_EXPIRE_DAYS = 3650;

        LocalDateTime expiryTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                .plusDays(REFRESH_TOKEN_EXPIRE_DAYS);
        return new RefreshTokenEntity(null, hashToken, expiryTime,false, userEntity);
    }

    private static String generateRandomToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private static String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }  catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
