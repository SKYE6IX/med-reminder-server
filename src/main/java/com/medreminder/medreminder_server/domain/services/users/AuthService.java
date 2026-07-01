package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.UseCase;

import java.util.Map;

public interface AuthService {

    AuthResponse registerUserWithEmail(RegisterUserRequest request);

    AuthResponse loginUserWithEmail(LoginRequest loginRequest);

    AuthResponse authorizeUserWithSocial(SocialAuthRequest socialAuthRequest);

    AuthResponse refreshToken(String token);

    ResetPasswordResponse changePassword(String userId, String oldPassword, String newPassword);

    ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest);

    Map<String, String> requestPasswordResetToken(String email);

    Map<String, String> verifyPasswordResetToken(String email, int token);

    void logoutUser(UserPrincipal userPrincipal);
}