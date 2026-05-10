package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.domain.models.billing.Plan;
import com.medreminder.medreminder_server.domain.models.billing.PlanType;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.billing.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.billing.mappers.PlanMapper;
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
    private final PlanMapper planMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PlanMapper planMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.planMapper = planMapper;
    }

    @Override
    public User createUser(RegisterUserRequest registerUserRequest) {

        User user = new User(null,
                registerUserRequest.getEmail(),
                registerUserRequest.getName(),
                registerUserRequest.getPassword());

//        Create a self profile for new user.
        Profile profile = new Profile(null, user.getName(), Relation.SELF, true);
        user.addProfiles(profile);

//        Create a default Plan for User.
        Plan freePlan = new Plan(null,
                PlanType.FREE,
                1,
                false,
                false,
                false);

        UserEntity userEntity = userMapper.toEntity(user);
        PlanEntity planEntity = planMapper.toEntity(freePlan, userEntity);
        userEntity.setPlan(planEntity);

        UserEntity newUser = userRepository.saveUser(userEntity);

        return userMapper.toDomain(newUser);
    }

    @Override
    public UserResponse updateUser(String userId, UpdateUserCommand updateUserCommand) {

        UserEntity managedUser = getUserEntity(userId);

        User domainUser = userMapper.toDomain(managedUser);

        domainUser.updateUser(updateUserCommand);

        managedUser.syncUserData(domainUser);

        userRepository.saveUser(managedUser);

        return new UserResponse(domainUser.getId(),
                domainUser.getEmail(),
                domainUser.getName(),
                domainUser.getDateOfBirth(),
                domainUser.getGender());
    }

    @Override
    public ProfileResponse createProfile(String userId, ProfileRequest profileRequest) {

        UserEntity managedUser = getUserEntity(userId);

        User domainUser = userMapper.toDomain(managedUser);

        Profile profile = new Profile(null, profileRequest.name(),
                Relation.valueOf(profileRequest.relation()), false);

        domainUser.addProfiles(profile);

        syncProfiles(domainUser.getProfiles(), managedUser);

        ProfileEntity newProfile = userRepository.saveUser(managedUser).getProfiles().getLast();

        return new ProfileResponse(newProfile.getId(),
                newProfile.getName(), newProfile.getRelation(),
                newProfile.isSelf());
    }

    @Override
    public List<ProfileResponse> getProfiles(String userId) {

        UserEntity managedUser = getUserEntity(userId);

        List<ProfileEntity> profileEntities = managedUser.getProfiles();

        return  profileEntities
                .stream()
                .map((profileEntity ->
                        new ProfileResponse(profileEntity.getId(),
                                profileEntity.getName(),
                                profileEntity.getRelation(),
                                profileEntity.isSelf())))
                .toList();
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity managedUser = getUserEntity(userId);

        userRepository.deleteUser(managedUser.getId());
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
                        .getOrDefault(p.getId(), userMapper.toEntity(p, managedEntity)))
                .toList();

        managedEntity.getProfiles().clear();
        managedEntity.getProfiles().addAll(syncedProfiles);
    }
}