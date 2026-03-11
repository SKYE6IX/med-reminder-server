package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public User toDomain(UserEntity userEntity) {

        if (userEntity == null) return null;

      User user = new User(userEntity.getId(),
               userEntity.getEmail(), userEntity.getHashPassword(),
               userEntity.getName(), userEntity.getDateOfBirth(),
               userEntity.getGender());

      userEntity.getProfiles().forEach(profile -> {
          user.addProfiles(toDomianProfile(profile));
      });

      return user;
    }

    public UserEntity toEntity(User user) {

        if(user == null) return null;

        UserEntity userEntity = new UserEntity.Builder()
                .withId(user.getId())
                .withEmail(user.getEmail())
                .withHashPassword(user.getHashPassword())
                .withName(user.getName())
                .withDateOfBirth(user.getDateOfBirth())
                .withGender(user.getGender())
                .build();

        List<ProfileEntity> profileEntities = user.getProfiles().
                stream().map(pe-> toEntityProfile(pe, userEntity)).toList();

        userEntity.setProfiles(profileEntities);

        return userEntity;
    }


    public Profile toDomianProfile(ProfileEntity profileEntity){
        if (profileEntity == null) return null;

        return new Profile(profileEntity.getId(),
                profileEntity.getName(),
                Relation.valueOf(profileEntity.getRelation()), profileEntity.isSelf());
    }

    public ProfileEntity toEntityProfile(Profile profile, UserEntity userEntity) {

        if (profile == null) return null;

        return new ProfileEntity(profile.getId(),
                profile.getName(), profile.getRelation().name(),
                profile.isSelf(), userEntity);

    }
}
