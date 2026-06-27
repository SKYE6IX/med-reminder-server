package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.application.exceptions.UserAlreadyExistsException;
import com.medreminder.medreminder_server.application.security.AppleAuth;
import com.medreminder.medreminder_server.application.services.S3Service;
import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final S3Service s3Service;
    private final AppleAuth appleAuth;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           SubscriptionMapper subscriptionMapper,
                           S3Service s3Service,
                           AppleAuth appleAuth) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.subscriptionMapper = subscriptionMapper;
        this.s3Service = s3Service;
        this.appleAuth = appleAuth;
    }

    @Override
    public UserEntity createUser(RegisterUserRequest registerUserRequest,
                           UserProvider userProvider) {

//        To make sure we don't try to create user that
//        already exist with a particular email, we should
//        check early and then return the right error.
        UserEntity userWithEmailExist = userRepository
                .findUserByEmail(registerUserRequest.getEmail())
                .orElse(null);

        if(userWithEmailExist != null) {
            throw new UserAlreadyExistsException(registerUserRequest.getEmail());
        }

        User user = new User(null,
                registerUserRequest.getEmail(),
                registerUserRequest.getName(),
                registerUserRequest.getPassword(),
                userProvider);

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
        PlanEntity planEntity = subscriptionMapper.toEntity(freePlan, userEntity);
        userEntity.setPlan(planEntity);

        return userRepository.saveUser(userEntity);
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

        return new ProfileResponse(
                newProfile.getId(),
                newProfile.getAvatarUrl(),
                newProfile.getName(),
                newProfile.getRelation(),
                newProfile.isSelf());
    }

    @Override
    public List<ProfileResponse> getProfiles(String userId) {

        UserEntity managedUser = getUserEntity(userId);

        List<ProfileEntity> profileEntities = managedUser.getProfiles();

        return  profileEntities
                .stream()
                .map((profileEntity ->
                        new ProfileResponse(
                                profileEntity.getId(),
                                profileEntity.getAvatarUrl(),
                                profileEntity.getName(),
                                profileEntity.getRelation(),
                                profileEntity.isSelf())))
                .toList();
    }

    @Override
    public Map<String, String> uploadProfileImage(MultipartFile file, String userId, String profileId) {

        UserEntity managedUser = getUserEntity(userId);
        Map<String, String> map = new HashMap<>();

        managedUser.getProfiles()
                .stream()
                .filter((profileEntity -> profileEntity.getId().equals(profileId)))
                .findFirst()
                .ifPresentOrElse(profileEntity -> {
                    if(profileEntity.getAvatarUrl() != null) {
                        s3Service.deleteFile(profileEntity.getAvatarUrl());
                    }
                    String url = s3Service.uploadFile(file);
                    map.put("url", url);
                    profileEntity.setAvatarUrl(url);
                    },
                        ()-> {throw new ResourceNotFoundException("Profile is not found!");}
                );
        userRepository.saveUser(managedUser);
        return map;
    }

    @Override
    public void deleteProfileImage(String userId, String profileId) {
        UserEntity managedUser = getUserEntity(userId);
        managedUser.getProfiles()
                .stream()
                .filter((profileEntity -> profileEntity.getId().equals(profileId)))
                .findFirst()
                .ifPresent(profileEntity -> {
                    if(profileEntity.getAvatarUrl() != null) {
                        s3Service.deleteFile(profileEntity.getAvatarUrl());
                        profileEntity.setAvatarUrl(null);
                    }
                });
        userRepository.saveUser(managedUser);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity managedUser = getUserEntity(userId);

//        Revoke app from user Apple's account.
        if(managedUser.getProvider().equals(UserProvider.APPLE.toString())){
            try {
                appleAuth.revokeAppleUserToken(managedUser.getAppleRevokeToken());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        managedUser.getProfiles()
                .forEach(profileEntity -> {
                    if(profileEntity.getAvatarUrl() != null) {
                        s3Service.deleteFile(profileEntity.getAvatarUrl());
                    }
                });
        userRepository.deleteUser(managedUser.getId());
    }

    @Override
    public void deleteProfile(String userId, String profileId) {

        UserEntity managedUser = getUserEntity(userId);

        Profile profileToRemove = managedUser.getProfiles()
                .stream()
                .filter((profileEntity -> profileEntity.getId().equals(profileId)))
                .findFirst()
                .map(profileEntity -> {
                    if(profileEntity.getAvatarUrl() != null) {
                        s3Service.deleteFile(profileEntity.getAvatarUrl());
                    }
                    return userMapper.toDomain(profileEntity);
                }).orElse(null);

        User domainUser = userMapper.toDomain(managedUser);
        domainUser.removeProfiles(profileToRemove);

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
                        .getOrDefault(p.getId(),
                                userMapper.toEntity(p, managedEntity)))
                .toList();

        managedEntity.getProfiles().clear();
        managedEntity.getProfiles().addAll(syncedProfiles);
    }
}