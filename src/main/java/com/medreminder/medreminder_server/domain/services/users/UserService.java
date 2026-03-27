package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.application.dtos.user.UserResponse;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.UseCase;

public interface UserService extends UseCase {

    User createUser(RegisterUserRequest registerUserRequest);

    User updateUser(String userId, UpdateUserCommand updateUserCommand);

    void deleteUser(String userId);

    Profile createProfile(String userId, ProfileRequest profileRequest);

    void deleteProfile(String userId, String profileId);

    UserResponse getUserById(String userId);
}
