package com.medreminder.medreminder_server.domain.services.users;


import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.application.exceptions.UserAlreadyExistsException;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final UserRepository userRepository;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           UserMapper userMapper,
                           JpaRefreshTokenRepository jpaRefreshTokenRepository,
                           UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
        this.userRepository = userRepository;
    }


    @Override
    public AuthResponse registerUserWithEmail(RegisterUserRequest request){

        UserEntity existingUser = userRepository.findUserByEmail(request.getEmail())
                .orElse(null);

        if( existingUser != null ){
           throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        request.updatePasswordToHash(hashPassword);

        User newUser = userService.createUser(request);

//      Generate access token for user
        String accessToken = jwtUtil.generateToken(newUser.getEmail(), newUser.getId());

//       Generate refresh token for user
        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken,
                userMapper.toEntity(newUser));

        jpaRefreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(newUser.getId(), newUser.getEmail(), accessToken, refreshToken);
    }

    @Override
    public AuthResponse loginUserWithEmail(LoginRequest loginRequest){

        String email = loginRequest.email();

        String password = loginRequest.password();

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        if (!auth.isAuthenticated() || userPrincipal == null) {
            throw new BadCredentialsException("Email or password is invalid");
        }

        jpaRefreshTokenRepository
                .findByUserIdAndRevokedFalse(userPrincipal.getId())
                .ifPresent(this::revokeRefreshToken);

//     Generate new token
        String accessToken = jwtUtil.generateToken(userPrincipal.getEmail(), userPrincipal.getId());

        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken,
                new UserEntity(userPrincipal.getId(),userPrincipal.getEmail(),null, null));

        jpaRefreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(userPrincipal.getId(), userPrincipal.getEmail(), accessToken, refreshToken);

    }

    @Override
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

        String accessToken = jwtUtil.generateToken(userEntity.getEmail(), userEntity.getId());

        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken, userEntity);

        jpaRefreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(userEntity.getId(), userEntity.getEmail(), accessToken, refreshToken);
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
