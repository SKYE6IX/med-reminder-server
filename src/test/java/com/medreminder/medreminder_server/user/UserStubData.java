package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;

import java.time.ZoneId;
import java.util.UUID;

public class UserStubData {

    public static User createUserWithId(String email, String name, String password) {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        User snubUser = UserStubData
                .createUser(userId.toString(), email, name, password);

        snubUser.addProfiles(UserStubData
                .createProfile(profileId.toString(), snubUser.getName(), "SELF", true));
        return snubUser;
    }

    public static Profile createProfileWithId(String name,
                                              String relation,
                                              boolean isSelf) {
        UUID profileId = UUID.randomUUID();
        return UserStubData.createProfile(profileId.toString(), name, relation, isSelf);
    }

    public static ProfileEntity createStubProfileEntity(){
        UserMapper userMapper = new UserMapper();
        return userMapper
                .toEntity(UserStubData.createProfileWithId("John",
                        "BROTHER", false), null);
    }

    public static User createUser(String id,
                                  String email,
                                  String name,
                                  String password) {
        return new User(
                id,
                email,
                name,
                password,
                UserProvider.LOCAL,
                ZoneId.of("Europe/Moscow").toString());
    }

    public static Profile createProfile(
            String id,
            String name,
            String relation,
            boolean isSelf) {
        return new Profile(
                id,
                name,
                Relation.valueOf(relation),
                isSelf);
    }
}
