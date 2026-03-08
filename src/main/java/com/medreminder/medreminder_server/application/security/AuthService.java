package com.medreminder.medreminder_server.application.security;


import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.model.User;
import com.medreminder.medreminder_server.infrastructure.entity.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.entity.UserEntity;
import com.medreminder.medreminder_server.infrastructure.mapper.UserMapper;
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

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();


    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    public AuthResponse registerUserWithEmail(RegisterUserRequest request){

        User existingUser = userService.findUserByEmail(request.getEmail());

        if( existingUser != null ){
           throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        request.updatePasswordToHash(hashPassword);

        User newUser = userService.createUser(request);

        //Generate token for user
        String token = jwtUtil.generateToken(newUser.getEmail());

        String refreshToken = generateRandomToken();

        RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(refreshToken,userMapper.toEntity(newUser));


//        Create a user with entity
//        Create access token and a new Refresh Token.
//        We need to send the raw access token and refresh token to User
//        We need to save the hash of refresh token into database.
//



//        Generate refreshToken

//        return new AuthResponse(newUser.getId(),newUser.getEmail(),token,refreshToken);
        return null;
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

        UserEntity userEntity = (UserEntity) auth.getPrincipal();

        User user = userMapper.toDomain(userEntity);

        String token = jwtUtil.generateToken(user.getEmail());

//        String refreshToken = jwtUtil.generateRefreshToken();

//        return new AuthResponse(user.getId(), user.getEmail(), token, refreshToken);
        return null;
    }


    private RefreshTokenEntity createRefreshTokenEntity(String rawToken, UserEntity userEntity) {
        String hashToken = hashRefreshToken(rawToken);;

        Instant expiryTime = Instant.now().plus(refreshExpiryDays, ChronoUnit.DAYS);

        return new RefreshTokenEntity(null,hashToken,expiryTime,false,userEntity);
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
