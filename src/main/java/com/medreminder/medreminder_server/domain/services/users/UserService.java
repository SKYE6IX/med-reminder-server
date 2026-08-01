package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserEntity createUser(RegisterUserRequest registerUserRequest,
                          String hashedPassword,
                          UserProvider userProvider);

    UserResponse getUser(String userId);

    UserResponse updateUser(String userId, UpdateUserCommand updateUserCommand);

    void deleteUser(String userId);

    ProfileResponse createProfile(String userId, ProfileRequest profileRequest);

    List<ProfileResponse> getProfiles(String userId);

    Map<String, String> uploadProfileImage(MultipartFile image, String userId, String profileId);

    void deleteProfileImage(String userId, String profileId);

    void deleteProfile(String userId, String profileId);
}
