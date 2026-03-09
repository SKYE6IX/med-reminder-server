package com.medreminder.medreminder_server.domain.service;

import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.UserRepository;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.model.User;
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

//        Setting ID to null here because it will be created on
//        creation level in DB
        User user = new User(null, registerUserRequest.getEmail(),
                registerUserRequest.getName(),
                registerUserRequest.getPassword());

        UserEntity userEntity = userRepository.save(userMapper.toEntity(user));

        return userMapper.toDomain(userEntity);
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
