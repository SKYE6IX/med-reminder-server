package com.medreminder.medreminder_server.domain;

import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.model.User;

import java.util.UUID;

public interface UserService {

    public User createUser(RegisterUserRequest registerUserRequest);

    public User updateUser(String id, UpdateUserCommand updateUserCommand);

    public User findUserByEmail(String email);
}
