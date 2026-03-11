package com.medreminder.medreminder_server.domain;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.User;

public interface UserService {

    public User createUser(RegisterUserRequest registerUserRequest);

    public User updateUser(User user, UpdateUserCommand updateUserCommand);

    public User findUserByEmail(String email);

    public void deleteUser(String id);

    public Profile createProfile(User user, ProfileRequest profileRequest);

    public void deleteProfile(User user, String id);
}
