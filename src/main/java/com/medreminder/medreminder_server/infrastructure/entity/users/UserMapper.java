package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.domain.models.medication.MedicationProfile;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private final MedicationMapper medicationMapper = new MedicationMapper();

    public UserEntity toEntity(User user) {
        if(user == null) return null;

        UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getHashPassword());

        if(!user.getProfiles().isEmpty()){

            List<ProfileEntity> profileEntities = user.getProfiles()
                    .stream()
                    .map(profile -> toEntity(profile, userEntity))
                    .toList();

            userEntity.getProfiles().addAll(profileEntities);
        }

       return userEntity;
    }

    public UserEntity toEntity(User domainUser, UserEntity managedEntity) {

        managedEntity.syncWithDomain(domainUser);

        syncProfiles(domainUser.getProfiles(), managedEntity);

        return managedEntity;
    }

    public ProfileEntity toEntity(Profile profile, UserEntity managedEntity) {

        if (profile == null) return null;

        return new ProfileEntity(
                profile.getId(),
                profile.getName(),
                profile.getRelation().name(),
                profile.isSelf(),
                managedEntity);
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

        if(!userEntity.getProfiles().isEmpty()) {
            List<Profile> profiles = userEntity.getProfiles()
                    .stream()
                    .map(this::toDomain)
                    .toList();
            user.getProfiles().addAll(profiles);
        }

        return user;
    }

    public Profile toDomain(ProfileEntity profileEntity) {

        if (profileEntity == null) return null;

        Profile profile = new Profile(
                profileEntity.getId(),
                profileEntity.getName(),
                Relation.valueOf(profileEntity.getRelation()),
                profileEntity.isSelf());

        if(!profileEntity.getMedicationProfile().isEmpty()) {
            List<MedicationProfile> medicationProfiles = profileEntity
                    .getMedicationProfile()
                    .stream()
                    .map(medicationMapper::toDomain)
                    .toList();
            profile.getMedicationProfiles().addAll(medicationProfiles);
        };

        return profile;
    }

    private void syncProfiles(List<Profile> domainProfiles, UserEntity managedEntity) {

        Map<String, ProfileEntity> existingProfiles = managedEntity.getProfiles()
                .stream()
                .collect(Collectors.toMap(ProfileEntity::getId, p -> p));

        List<ProfileEntity> syncedProfiles = domainProfiles
                .stream()
                .map(p -> existingProfiles
                        .getOrDefault(p.getId(), toEntity(p, managedEntity)))
                .toList();

        managedEntity.getProfiles().clear();

        managedEntity.getProfiles().addAll(syncedProfiles);
    }
}
