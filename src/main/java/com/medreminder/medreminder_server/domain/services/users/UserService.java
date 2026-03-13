package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.User;

public interface UserService {

    public User createUser(RegisterUserRequest registerUserRequest);

    public User updateUser(String userId, UpdateUserCommand updateUserCommand);

    public void deleteUser(String id);

    public Profile createProfile(String userId, ProfileRequest profileRequest);

    public Profile deleteProfile(String userId, String id);
}
