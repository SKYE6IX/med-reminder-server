package com.medreminder.medreminder_server.domain.services.users;


import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.exceptions.UserAlreadyExistsException;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.application.services.EmailService;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;
    private final EmailService emailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserService userService,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           UserRepository userRepository,
                           TokenManager tokenManager,
                           EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse registerUserWithEmail(RegisterUserRequest request){

        UserEntity existingUser = userRepository.findUserByEmail(request.email())
                .orElse(null);

        if( existingUser != null ) {
           throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        String hashPassword = passwordEncoder.encode(request.password());

        UserEntity newUser = userService.createUser(request, hashPassword, UserProvider.LOCAL);

        return getAuthResponse(newUser);
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

        UserEntity loginUser = userRepository.findUserById(userPrincipal.getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + email));

        tokenManager.revokeRefreshToken(loginUser.getId());

        return getAuthResponse(loginUser);
    }

    @Override
    public AuthResponse authorizeUserWithSocial(SocialAuthRequest socialAuthRequest) {
        final boolean isAppleTokenValid = tokenManager
                .validateAppleToken(socialAuthRequest.jwtToken());

        if(!isAppleTokenValid) {
            throw new BadCredentialsException("Invalid token from social authentication!");
        }
//        Check if user already sign up with social using their
//        providerId.
        UserEntity user = userRepository
                .findUserByProviderId(socialAuthRequest.providerId())
                .orElse(null);

//        If user null, it means this the first time user,
//        is using a social media authorization.
        if(user == null) {
            final String revokeToken = tokenManager
                    .getAppleRevokeToken(socialAuthRequest.authorizationCode());

//            For some random reason, user that already exist decided to use
//            the social authentication. We check if the incoming email exist
//            and update their data.
            UserEntity userWithEmailExist = userRepository
                    .findUserByEmail(socialAuthRequest.email())
                    .orElse(null);

            if(userWithEmailExist != null) {
                userWithEmailExist.setProvider(socialAuthRequest.provider());
                userWithEmailExist.setProviderId(socialAuthRequest.providerId());
                userWithEmailExist.setAppleRevokeToken(revokeToken);

                tokenManager.revokeRefreshToken(userWithEmailExist.getId());
                return getAuthResponse(userWithEmailExist);
            } else {
                RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                        socialAuthRequest.email(),
                        socialAuthRequest.fullName(),
                        null,
                        socialAuthRequest.timeZone()
                );
                UserEntity newUser = userService.createUser(registerUserRequest,
                        null, UserProvider.valueOf(socialAuthRequest.provider()));

                newUser.setProviderId(socialAuthRequest.providerId());
                newUser.setAppleRevokeToken(revokeToken);
                return  getAuthResponse(newUser);
            }
        }

//        If user is found with the providerId,
//        then this user exist.
        tokenManager.revokeRefreshToken(user.getId());
        return getAuthResponse(user);
    }

    @Override
    public AuthResponse refreshAccessToken(String token) {
     return tokenManager.refreshAccessToken(token);
    }

    @Override
    public ResetPasswordResponse changePassword(String userId, String oldPassword, String newPassword) {

        UserEntity existingUser = userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + userId));

        User domainUser = userMapper.toDomain(existingUser);

        boolean passwordMatch = passwordEncoder.matches(oldPassword ,domainUser.getHashPassword());

        if (!passwordMatch) {
            throw new BadCredentialsException("Invalid old password!");
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);

        domainUser.updatePassword(newPasswordHash);

        existingUser.syncUserData(domainUser);

        // Revoked the existing token
        tokenManager.revokeRefreshToken(domainUser.getId());

        userRepository.saveUser(existingUser);

        return new ResetPasswordResponse("success", "Successfully change your password!");
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {

        UserEntity existingUser = userRepository.findUserByEmail(resetPasswordRequest.email())
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        boolean isValidToken = tokenManager
                .validatePasswordResetToken(String.valueOf(resetPasswordRequest.token()),
                existingUser.getPasswordResetToken());

        if (!isValidToken) {
            throw new BadCredentialsException("Invalid token!");
        }

        User domainUser = userMapper.toDomain(existingUser);

        String newPasswordHash = passwordEncoder.encode(resetPasswordRequest.newPassword());

        domainUser.updatePassword(newPasswordHash);

        existingUser.syncUserData(domainUser);
        existingUser.redeemPasswordResetToken();

        // Revoked the existing token
        tokenManager.revokeRefreshToken(domainUser.getId());

        userRepository.saveUser(existingUser);

        return new ResetPasswordResponse("success", "Successfully reset your password!");
    }

    @Override
    public Map<String, String> requestPasswordResetToken(String email) {

        UserEntity existingUser = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        final int token = tokenManager.generatePasswordResetRawToken();
        final String hashToken = tokenManager.getPasswordResetHashToken(String.valueOf(token));

        existingUser.issuePasswordResetToken(hashToken);

        final String EMAIL_SUBJECT = "Ваш код подтверждения";
        final String EMAIL_TEMPLATE= "otp-email-template.ftl";

        Map<String, String> model = new HashMap<>();
        model.put("token",String.valueOf(token));

        emailService.sendTemplateEmail(existingUser.getEmail(),
                EMAIL_SUBJECT, EMAIL_TEMPLATE, model);

        userRepository.saveUser(existingUser);

        Map<String, String> result = new HashMap<>();

        result.put("status", "success");

        return result;
    }

    @Override
    public Map<String, String> verifyPasswordResetToken(String email, int token) {

        final long EXPIRE_MINUTES = 30;

        UserEntity existingUser = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        boolean isValidToken = tokenManager.validatePasswordResetToken(String.valueOf(token),
                existingUser.getPasswordResetToken());

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        boolean isExpired = existingUser.getPasswordResetIssuedAt()
                .plusMinutes(EXPIRE_MINUTES).isBefore(now);

        if( isExpired || !isValidToken) {
            throw new BadCredentialsException("Invalid token!");
        }

        Map<String, String> result = new HashMap<>();
        result.put("status", "success");

        return result;
    }

    @Override
    public void logoutUser(UserPrincipal userPrincipal) {
      tokenManager.revokeRefreshToken(userPrincipal.getId());
    }

    private AuthResponse getAuthResponse(UserEntity user) {
        String accessToken = tokenManager.generateAccessToken(user.getEmail(), user.getId());
        String refreshToken = tokenManager.generateRawRefreshToken();
        tokenManager.saveHashRefreshToken(refreshToken, user);

        user.updateLastLoginAt(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        userRepository.saveUser(user);
        return new AuthResponse(user.getId(), user.getEmail(), accessToken, refreshToken);
    }
}
