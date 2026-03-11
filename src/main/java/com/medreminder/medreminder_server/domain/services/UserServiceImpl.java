package com.medreminder.medreminder_server.domain.services;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.UserRepository;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;

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
    public User updateUser(User user, UpdateUserCommand updateUserCommand) {

        user.updateUser(updateUserCommand);

        userRepository.save(userMapper.toEntity(user));

        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        return userMapper.toDomain(userRepository.findUserByEmail(email).orElse(null));
    }

    @Override
    public Profile createProfile(User user, ProfileRequest profileRequest) {

        Profile profile = new Profile(null, profileRequest.fullName(),
                Relation.valueOf(profileRequest.relation()), false);

        user.addProfiles(profile);

        return userRepository
                .save(userMapper.toEntity(user))
                .getProfiles()
                .stream()
                .filter(pe ->
                        pe.getName().equals(profile.getName())
                        && Relation.valueOf(pe.getRelation()) == profile.getRelation())
                .findFirst()
                .map(userMapper::toDomianProfile)
                .orElseThrow(()-> new RuntimeException("Profile not found!"));
    }

    @Override
    public void deleteUser(String id) {
    }

    @Override
    public void deleteProfile(User user, String id) {

        Profile profileToDelete = user.getProfiles()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        user.removeProfiles(profileToDelete);

        userRepository.save(userMapper.toEntity(user));
    }
}
