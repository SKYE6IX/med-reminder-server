package com.medreminder.medreminder_server.domain.services.users;


import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.application.exceptions.UserAlreadyExistsException;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           UserRepository userRepository,
                           TokenManager tokenManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }

    @Override
    public AuthResponse registerUserWithEmail(RegisterUserRequest request){

        UserEntity existingUser = userRepository.findUserByEmail(request.getEmail())
                .orElse(null);

        if( existingUser != null ) {
           throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        request.updatePasswordToHash(hashPassword);

        UserEntity newUser = userService.createUser(request, UserProvider.LOCAL);

//      Generate access token for user
        String accessToken = tokenManager.generateAccessToken(newUser.getEmail(), newUser.getId());

//       Generate refresh token for user
        String refreshToken = tokenManager.generateRefreshToken();

        newUser.updateLastLoginAt(LocalDateTime.now());

        tokenManager.storeRefreshToken(refreshToken,newUser);
        userRepository.saveUser(newUser);

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
            throw new BadCredentialsException("Email or password is invalid!");
        }

        tokenManager.revokeRefreshToken(userPrincipal.getId());
//     Generate new token
        String accessToken = tokenManager.generateAccessToken(userPrincipal.getEmail(), userPrincipal.getId());

        String refreshToken = tokenManager.generateRefreshToken();

        UserEntity loginUser = userRepository.findUserById(userPrincipal.getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + email));

        loginUser.updateLastLoginAt(LocalDateTime.now());
        tokenManager.storeRefreshToken(refreshToken,loginUser);
        userRepository.saveUser(loginUser);

        return new AuthResponse(userPrincipal.getId(), userPrincipal.getEmail(), accessToken, refreshToken);
    }

    @Override
    public AuthResponse authorizeUserWithSocial(SocialAuthRequest socialAuthRequest) {

        boolean isAppleTokenValid = tokenManager
                .validateAppleToken(socialAuthRequest.jwtToken());

        if(!isAppleTokenValid) {
            throw new BadCredentialsException("Invalid token from social authentication!");
        }

        UserEntity user = userRepository.findUserByProviderId(socialAuthRequest.providerId())
                .orElse(null);

//        If user null, mean this user is new
        if(user == null) {

            RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                    socialAuthRequest.email(),
                    socialAuthRequest.fullName(),
                    null
            );
            UserEntity newUser = userService.createUser(registerUserRequest,
                    UserProvider.valueOf(socialAuthRequest.provider()));
            newUser.setProviderId(socialAuthRequest.providerId());

            String accessToken = tokenManager.generateAccessToken(newUser.getEmail(),
                    newUser.getId());
            String refreshToken = tokenManager.generateRefreshToken();

            newUser.updateLastLoginAt(LocalDateTime.now());
            tokenManager.storeRefreshToken(refreshToken,newUser);
            userRepository.saveUser(newUser);
            return new AuthResponse(newUser.getId(), newUser.getEmail(), accessToken, refreshToken);
        }


//        If user is found with the providerId, then this user exist.
        tokenManager.revokeRefreshToken(user.getId());

        String accessToken = tokenManager.generateAccessToken(user.getEmail(),
                user.getId());
        String refreshToken = tokenManager.generateRefreshToken();

        user.updateLastLoginAt(LocalDateTime.now());
        tokenManager.storeRefreshToken(refreshToken,user);
        userRepository.saveUser(user);
        return new AuthResponse(user.getId(), user.getEmail(), accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String token) {
     return tokenManager.refreshToken(token);
    }

    @Override
    public ResetPasswordResponse resetPassword(String userId, String oldPassword, String newPassword) {

        UserEntity existingUser = userRepository.findUserById(userId)
                .orElse(null);

        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        User domainUser = userMapper.toDomain(existingUser);

        boolean passwordMatch = passwordEncoder.matches(oldPassword,domainUser.getHashPassword());

        if (!passwordMatch) {
            throw new BadCredentialsException("Invalid old password!");
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);

        domainUser.updatePassword(newPasswordHash);

        existingUser.syncUserData(domainUser);

        // Revoked the existing token
        tokenManager.revokeRefreshToken(domainUser.getId());

        userRepository.saveUser(existingUser);

        return new ResetPasswordResponse("success", "Successfully reset your password!");
    }

    @Override
    public void logoutUser(UserPrincipal userPrincipal) {
      tokenManager.revokeRefreshToken(userPrincipal.getId());
    }
}
