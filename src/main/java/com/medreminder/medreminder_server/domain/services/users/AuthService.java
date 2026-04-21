package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.UseCase;

public interface AuthService extends UseCase {

    AuthResponse registerUserWithEmail(RegisterUserRequest request);

    AuthResponse loginUserWithEmail(LoginRequest loginRequest);

    AuthResponse refreshToken(String token);

    void logoutUser(UserPrincipal userPrincipal);
}
