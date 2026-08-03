package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.application.exceptions.UserAlreadyExistsException;
import com.medreminder.medreminder_server.application.security.AppleAuth;
import com.medreminder.medreminder_server.application.services.S3Service;
import com.medreminder.medreminder_server.application.services.TelemetryService;
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

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final S3Service s3Service;
    private final AppleAuth appleAuth;

    final int DEFAULT_MAX_MEDICATIONS = 2;

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
                                 String hashedPassword, UserProvider userProvider) {
//        To make sure we don't try to create user that
//        already exist with a particular email, we should
//        check early and then return the right error.
        var existUser = userRepository
                .findUserByEmail(registerUserRequest.email())
                .orElse(null);

        if (existUser != null) {
            throw new UserAlreadyExistsException(registerUserRequest.email());
        }

        User user = new User(null,
                registerUserRequest.email(),
                registerUserRequest.name(),
                hashedPassword,
                userProvider,
                registerUserRequest.timeZone());

//        Create a self profile for new user.
        Profile profile = new Profile(null, user.getName(), Relation.SELF, true);
        user.addProfiles(profile);

//        Create a default Plan for User.
        Plan freePlan = new Plan(null,
                PlanType.FREE,
                DEFAULT_MAX_MEDICATIONS,
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

        UserEntity managedUser = findUserById(userId);

        User domainUser = userMapper.toDomain(managedUser);

        domainUser.updateUser(updateUserCommand);

        managedUser.syncUserData(domainUser);

        userRepository.saveUser(managedUser);


        String dob = domainUser.getDateOfBirth() != null
                ? domainUser.getDateOfBirth().format(DateTimeFormatter.BASIC_ISO_DATE) : null;

        return new UserResponse(domainUser.getId(),
                domainUser.getEmail(),
                domainUser.getName(),
                dob,
                domainUser.getGender());
    }

    @Override
    public ProfileResponse createProfile(String userId, ProfileRequest profileRequest) {
        UserEntity managedUser = findUserById(userId);

        Profile profile = new Profile(null, profileRequest.name(),
                Relation.valueOf(profileRequest.relation()), false);

        var profileEntity = userMapper.toEntity(profile, managedUser);

        managedUser.getProfiles().add(profileEntity);

        var newProfile = userRepository.saveUser(managedUser).getProfiles().getLast();

        return new ProfileResponse(
                newProfile.getId(),
                newProfile.getAvatarUrl(),
                newProfile.getName(),
                newProfile.getRelation(),
                newProfile.isSelf());
    }

    @Override
    public List<ProfileResponse> getProfiles(String userId) {

        UserEntity managedUser = findUserById(userId);

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
        UserEntity managedUser = findUserById(userId);
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
                    },()-> {
                    throw new ResourceNotFoundException("Profile is not found!");
                }
                );
        userRepository.saveUser(managedUser);
        return map;
    }


    @Override
    public void deleteUser(String userId) {
        UserEntity managedUser = findUserById(userId);

//        Revoke app from user Apple's account.
        if(managedUser.getProvider().equals(UserProvider.APPLE.toString())){
            try {
                appleAuth.revokeAppleUserToken(managedUser.getAppleRevokeToken());
            } catch (Exception e) {
                TelemetryService.captureException(e);
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
        UserEntity managedUser = findUserById(userId);

        ProfileEntity profileToRemove = managedUser.getProfiles()
                .stream()
                .filter(profileEntity -> profileEntity.getId().equals(profileId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Profile is not found!"));

        if(profileToRemove.getAvatarUrl() != null) {
            s3Service.deleteFile(profileToRemove.getAvatarUrl());
        }

//    We can call remove on manage user because we set up
//    orphan removal 'true' in relationship map.
        managedUser.getProfiles().remove(profileToRemove);
        userRepository.saveUser(managedUser);
    }

    @Override
    public void deleteProfileImage(String userId, String profileId) {
        UserEntity managedUser = findUserById(userId);

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
    public UserResponse getUser(String userId) {
        UserEntity userEntity = findUserById(userId);

        String dob = userEntity.getDateOfBirth() != null
                ? userEntity.getDateOfBirth().format(DateTimeFormatter.BASIC_ISO_DATE) : null;

        return new UserResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName(),
                dob,
                userEntity.getGender());
    }

    private UserEntity findUserById(String userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found!"));
    }
}