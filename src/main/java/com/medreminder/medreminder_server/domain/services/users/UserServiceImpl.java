package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.application.dtos.user.UserResponse;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(RegisterUserRequest registerUserRequest) {

        User user = new User(null,
                registerUserRequest.getEmail(),
                registerUserRequest.getName(),
                registerUserRequest.getPassword());

        Profile profile = new Profile(null, user.getName(), Relation.SELF, true);

        user.addProfiles(profile);

        UserEntity newUser = userRepository.saveUser(userMapper.toEntity(user));

        return userMapper.toDomain(newUser);
    }

    @Override
    public User updateUser(String userId, UpdateUserCommand updateUserCommand) {

        UserEntity managedUser = getUserEntity(userId);

        User domainUser = userMapper.toDomain(managedUser);

        domainUser.updateUser(updateUserCommand);

        managedUser.updateUserDetails(domainUser);

        userRepository.saveUser(managedUser);

        return domainUser;
    }

    @Override
    public Profile createProfile(String userId, ProfileRequest profileRequest) {

        UserEntity managedUser = getUserEntity(userId);

        User domainUser = userMapper.toDomain(managedUser);

        Profile profile = new Profile(null, profileRequest.fullName(),
                Relation.valueOf(profileRequest.relation()), false);

        domainUser.addProfiles(profile);

        syncProfiles(domainUser.getProfiles(), managedUser);

        ProfileEntity newProfile = userRepository.saveUser(managedUser).getProfiles().getLast();

        return userMapper.toDomain(newProfile);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public void deleteProfile(String userId, String profileId) {

        UserEntity managedUser = getUserEntity(userId);

        User domainUser = userMapper.toDomain(managedUser);

        domainUser.getProfiles()
                .stream()
                .filter(profile -> profile.getId().equals(profileId))
                .findFirst().ifPresent(domainUser::removeProfiles);

        syncProfiles(domainUser.getProfiles(), managedUser);

        userRepository.saveUser(managedUser);
    }


    @Override
    public UserResponse getUserById(String userId) {
        UserEntity userEntity = getUserEntity(userId);
        return new UserResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName(),
                userEntity.getDateOfBirth(),
                userEntity.getGender());
    }

    private UserEntity getUserEntity(String userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));
    }

    private void syncProfiles(List<Profile> domainProfiles,
                              UserEntity managedEntity) {

        Map<String, ProfileEntity> existingProfiles = managedEntity
                .getProfiles()
                .stream()
                .collect(Collectors.toMap(ProfileEntity::getId, p -> p));

        List<ProfileEntity> syncedProfiles = domainProfiles
                .stream()
                .map(p -> existingProfiles
                        .getOrDefault(p.getId(), userMapper.toEntity(p)))
                .toList();

        managedEntity.getProfiles().clear();
        managedEntity.getProfiles().addAll(syncedProfiles);
    }
}