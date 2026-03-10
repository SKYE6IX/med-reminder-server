package com.medreminder.medreminder_server.domain.services;

import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.UserRepository;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.models.Profile;
import com.medreminder.medreminder_server.domain.models.Relation;
import com.medreminder.medreminder_server.domain.models.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.mapper.UserMapper;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userMapper = new UserMapper();
    }

    @Override
    public User createUser(RegisterUserRequest registerUserRequest) {

        User user = new User(null, registerUserRequest.getEmail(),
                registerUserRequest.getName(),
                registerUserRequest.getPassword());

        Profile profile = new Profile(null, user.getName(), Relation.SELF, true);

        user.addProfiles(profile);

        UserEntity newUser = userRepository.save(userMapper.toEntity(user));

        return userMapper.toDomain(newUser);
    }

    @Override
    public User updateUser(String id, UpdateUserCommand updateUserCommand) {

        User user = getUser(id);

        user.updateUser(updateUserCommand);

        userRepository.save(userMapper.toEntity(user));

        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        return userMapper.toDomain(userRepository.findUserByEmail(email).orElse(null));
    }

    private  User getUser(String id) {
        return userMapper.toDomain(userRepository.findById(id).orElse(null));
    }
}
