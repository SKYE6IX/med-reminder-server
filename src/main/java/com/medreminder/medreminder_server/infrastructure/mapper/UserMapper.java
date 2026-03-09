package com.medreminder.medreminder_server.infrastructure.mapper;


import com.medreminder.medreminder_server.domain.model.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserMapper() {
    }

    public User toDomain(UserEntity userEntity){
        if (userEntity == null) return null;

       return new User(userEntity.getId(),
               userEntity.getEmail(), userEntity.getHashPassword(),
               userEntity.getName(), userEntity.getDateOfBirth(),
               userEntity.getGender());
    }

    public UserEntity toEntity(User user){
        if(user == null) return null;
        return new UserEntity(user.getId(),user.getEmail(), user.getName(), user.getHashPassword());
    }

}
