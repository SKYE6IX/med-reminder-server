package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.ResetPasswordResponse;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.UseCase;

public interface AuthService extends UseCase {

    AuthResponse registerUserWithEmail(RegisterUserRequest request);

    AuthResponse loginUserWithEmail(LoginRequest loginRequest);

    AuthResponse refreshToken(String token);

    ResetPasswordResponse resetPassword(String userId, String oldPassword, String newPassword);

    void logoutUser(UserPrincipal userPrincipal);
}



//TODO 1:
// On occasion where user want to replace their password;
// Changing password mean getting the current user, and then after the operation
// we need to sign them out, so they can then use the new password to sign in again.
// Requirements: Current userID; Current Password; New Password; ✅


//TODO 2:
// On occasion where user forget their password;
// This situation a bit different. We don't need user to be sign in.
// To change their password, we need to set up an otp code, which we will use to verify it's
// the user required to change their password. There are multiple stages here:
// Stage 1: User send their valid email that exist in database, and we send an otp to this
//          email.
// Stage 2: User then send the otp we sent to them back to us, and then we confirm if the
//          otp is valid, we then send a token that going to be used to create a new password.
//          this token will be included in the request user will make when they send their new
//          password.
// Step 3: User send a new password along with verify token password which will then use to update their
//          password. They can then now login with the new password, or return a new AuthResponse.
//          This will log them in back in the app.