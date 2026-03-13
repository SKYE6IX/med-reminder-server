package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toDomain(UserEntity userEntity) {

        if (userEntity == null) return null;

      User user = new User(userEntity.getId(),
               userEntity.getEmail(),
              userEntity.getHashPassword(),
               userEntity.getName(),
              userEntity.getDateOfBirth(),
               userEntity.getGender());
      userEntity.getProfiles().forEach(profile -> {
          user.addProfiles(toDomianProfile(profile));
      });

      return user;
    }

    public UserEntity toEntity(User user) {

        if(user == null) return null;

        UserEntity userEntity = new UserEntity(user.getId(),
                user.getEmail(), user.getName(),
                user.getHashPassword());

       List<ProfileEntity> profileEntities = user.getProfiles().
                stream().map(pe-> toEntityProfile(pe, userEntity))
               .toList();

       userEntity.getProfiles().addAll(profileEntities);

       return userEntity;
    }

    public UserEntity toManagedEntity(User domainUser, UserEntity managedEntity) {

        managedEntity.syncWithDomain(domainUser);

        syncProfiles(domainUser.getProfiles(), managedEntity);

        return managedEntity;
    }

    public Profile toDomianProfile(ProfileEntity profileEntity) {
        if (profileEntity == null) return null;

        return new Profile(profileEntity.getId(),
                profileEntity.getName(),
                Relation.valueOf(profileEntity.getRelation()),
                profileEntity.isSelf());
    }

    public ProfileEntity toEntityProfile(Profile profile, UserEntity userEntity) {
        if (profile == null) return null;

        return new ProfileEntity(profile.getId(),
                profile.getName(),
                profile.getRelation().name(),
                profile.isSelf(), userEntity);
    }

    private void syncProfiles(List<Profile> domainProfiles, UserEntity managedEntity) {

        Map<String, ProfileEntity> existingProfiles = managedEntity.getProfiles()
                .stream().collect(Collectors.toMap(ProfileEntity::getId, p -> p));

        List<ProfileEntity>  syncedProfiles = domainProfiles.stream()
                .map(p -> existingProfiles
                        .getOrDefault(p.getId(), toEntityProfile(p,managedEntity)))
                .toList();

        managedEntity.getProfiles().clear();
        managedEntity.getProfiles().addAll(syncedProfiles);
    }
}
