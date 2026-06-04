package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;

import java.util.UUID;

public class UserStubData {

    public static User createStubUserWithId(String email, String name, String password) {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(email,name,password);
        User snubUser = new User(
                userId.toString(),
                registerUserRequest.getEmail(),
                registerUserRequest.getName(),
                registerUserRequest.getPassword(),
                UserProvider.LOCAL);

        snubUser.addProfiles(new Profile(profileId.toString(),
                registerUserRequest.getName(), Relation.SELF, true));

        return snubUser;
    }


    public static Profile createStubProfileWithId(String name,
                                                  String relation,
                                                  boolean isSelf) {
        UUID profileId = UUID.randomUUID();
        ProfileRequest profileRequest = new ProfileRequest(name,relation);
        return new Profile(
                profileId.toString(),
                profileRequest.name(),
                Relation.valueOf(profileRequest.relation()),
                isSelf);
    }

    public static ProfileEntity createStubProfileEntity(){
        UserMapper userMapper = new UserMapper();
        return userMapper
                .toEntity(UserStubData.createStubProfileWithId("John",
                        "BROTHER", false), null);
    }
}
