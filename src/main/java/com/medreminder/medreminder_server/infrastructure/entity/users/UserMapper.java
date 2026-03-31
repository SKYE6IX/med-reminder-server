package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.domain.models.medication.MedicationProfile;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        if(user == null) return null;

        UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getHashPassword());

        List<ProfileEntity> profileEntities = user
                .getProfiles()
                .stream()
                .map(profile ->  toEntity(profile, userEntity))
                .toList();

        userEntity.getProfiles().addAll(profileEntities);

       return userEntity;
    }

    public ProfileEntity toEntity(Profile profile) {

        if (profile == null) return null;
        return new ProfileEntity(
                profile.getId(),
                profile.getName(),
                profile.getRelation().name(),
                profile.isSelf());
    }

    public ProfileEntity toEntity(Profile profile,
                                  UserEntity userEntity ) {
        if (profile == null) return null;
        return new ProfileEntity(
                profile.getId(),
                profile.getName(),
                profile.getRelation().name(),
                profile.isSelf(),
                userEntity);
    }

    public User toDomain(UserEntity userEntity) {

        if (userEntity == null) return null;

        User user = new User(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getHashPassword(),
                userEntity.getName(),
                userEntity.getDateOfBirth(),
                userEntity.getGender());

        List<Profile> profiles = userEntity
                .getProfiles()
                .stream()
                .map(this::toDomain)
                .toList();

        user.getProfiles().addAll(profiles);
        return user;
    }

    public Profile toDomain(ProfileEntity profileEntity) {

        if (profileEntity == null) return null;

        return new Profile(
                profileEntity.getId(),
                profileEntity.getName(),
                Relation.valueOf(profileEntity.getRelation()),
                profileEntity.isSelf());
    }
}
