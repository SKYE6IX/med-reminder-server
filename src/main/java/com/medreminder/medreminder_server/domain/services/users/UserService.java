package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.UseCase;

import java.util.List;

public interface UserService extends UseCase {

    User createUser(RegisterUserRequest registerUserRequest);

    UserResponse getUserById(String userId);

    UserResponse updateUser(String userId, UpdateUserCommand updateUserCommand);

    void deleteUser(String userId);

    ProfileResponse createProfile(String userId, ProfileRequest profileRequest);

    List<ProfileResponse> getProfiles(String userId);

    void deleteProfile(String userId, String profileId);
}
