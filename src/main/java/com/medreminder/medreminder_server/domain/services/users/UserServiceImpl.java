package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

        UserEntity newUser = userRepository.saveUser(userMapper.toEntity(user));

        return userMapper.toDomain(newUser);
    }

    @Override
    public User updateUser(String userId, UpdateUserCommand updateUserCommand) {

        UserEntity userEntity = getUserEntity(userId);

        User domainUser = userMapper.toDomain(userEntity);

        domainUser.updateUser(updateUserCommand);

        userRepository.saveUser(userMapper.toManagedEntity(domainUser, userEntity));

        return domainUser;
    }

    @Override
    public Profile createProfile(String userId, ProfileRequest profileRequest) {

        UserEntity userEntity = getUserEntity(userId);

        User domainUser = userMapper.toDomain(userEntity);

        Profile profile = new Profile(null, profileRequest.fullName(),
                Relation.valueOf(profileRequest.relation()), false);

        domainUser.addProfiles(profile);

        return userRepository
                .saveUser(userMapper.toManagedEntity(domainUser, userEntity))
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
    public Profile deleteProfile(String userId, String id) {

        UserEntity userEntity = getUserEntity(userId);

        User domainUser = userMapper.toDomain(userEntity);

        Profile profileToDelete = domainUser.getProfiles()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        if( profileToDelete == null ){
            return null;
        }

        domainUser.removeProfiles(profileToDelete);

        userRepository.saveUser(userMapper.toManagedEntity(domainUser, userEntity));

        return profileToDelete;
    }

    private UserEntity getUserEntity(String userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));
    }
}