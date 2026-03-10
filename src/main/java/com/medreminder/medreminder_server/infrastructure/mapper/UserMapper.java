package com.medreminder.medreminder_server.infrastructure.mapper;


import com.medreminder.medreminder_server.domain.models.Profile;
import com.medreminder.medreminder_server.domain.models.Relation;
import com.medreminder.medreminder_server.domain.models.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserMapper() {
    }

    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) return null;

      return new User(userEntity.getId(),
               userEntity.getEmail(), userEntity.getHashPassword(),
               userEntity.getName(), userEntity.getDateOfBirth(),
                userEntity.getGender());
    }

    public UserEntity toEntity(User user){
        if(user == null) return null;

        List<ProfileEntity> profiles = user.getProfiles()
                .stream().map(this::toEntityProfile).toList();

        return new UserEntity(user.getId(),
                user.getEmail(),
                user.getName(), user.getHashPassword(), profiles);
    }

    public Profile toDomianProfile(ProfileEntity profileEntity){
        if (profileEntity == null) return null;

        return new Profile(profileEntity.getId(),
                profileEntity.getName(),
                Relation.valueOf(profileEntity.getRelation()), profileEntity.isSelf());
    }

    public ProfileEntity toEntityProfile(Profile profile){
        if (profile == null) return null;

        return new ProfileEntity(profile.getId(),
                profile.getName(), profile.getRelation().name(),
                profile.isSelf());
    }

}
