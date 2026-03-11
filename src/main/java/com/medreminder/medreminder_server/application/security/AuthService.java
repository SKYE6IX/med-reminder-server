package com.medreminder.medreminder_server.application.security;


import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class AuthService {

    @Value("${jwt.refresh-expiry-days}")
    private int refreshExpiryDays;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UserMapper userMapper,
                       JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    public AuthResponse registerUserWithEmail(RegisterUserRequest request){

        User existingUser = userService.findUserByEmail(request.getEmail());

        if( existingUser != null ){
           throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        request.updatePasswordToHash(hashPassword);

        User newUser = userService.createUser(request);

        // Generate access token for user
        String token = jwtUtil.generateToken(newUser.getEmail());

        // Generate refresh token for user
        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken,
                userMapper.toEntity(newUser));

        jpaRefreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(newUser.getId(), newUser.getEmail(), token, refreshToken);
    }


    public AuthResponse loginUserWithEmail(LoginRequest loginRequest){

        String email = loginRequest.email();

        String password = loginRequest.password();

        User existingUser = userService.findUserByEmail(email);
        if (existingUser == null){
            throw new UsernameNotFoundException("Invalid email or password");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        if(!auth.isAuthenticated()){
            throw new BadCredentialsException("Email or password is invalid");
        }

        User user = userMapper.toDomain((UserEntity) auth.getPrincipal());

        jpaRefreshTokenRepository
                .findByUserIdAndRevokedFalse(user.getId())
                .ifPresent(this::revokeRefreshToken);

//      Generate new token
        String token = jwtUtil.generateToken(user.getEmail());

        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken,
                userMapper.toEntity(user));

        jpaRefreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(user.getId(), user.getEmail(), token, refreshToken);
    }



    public AuthResponse refreshToken(String token) {

        String hashToken = hashRefreshToken(token);

        RefreshTokenEntity existingRefreshToken = jpaRefreshTokenRepository.findByHashToken(hashToken);

        if (existingRefreshToken == null) {
            throw new BadCredentialsException("Invalid refresh token!");
        } else if (existingRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Invalid refresh token!");
        }

        existingRefreshToken.setRevoked(true);

        jpaRefreshTokenRepository.save(existingRefreshToken);

        UserEntity userEntity = existingRefreshToken.getUser();

        String accessToken = jwtUtil.generateToken(userEntity.getEmail());

        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken, userEntity);

        jpaRefreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(userEntity.getId(), userEntity.getEmail(), accessToken, refreshToken);
    }

    private RefreshTokenEntity createRefreshTokenEntity(String rawToken, UserEntity userEntity) {
        String hashToken = hashRefreshToken(rawToken);;

        Instant expiryTime = Instant.now().plus(refreshExpiryDays, ChronoUnit.DAYS);

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
