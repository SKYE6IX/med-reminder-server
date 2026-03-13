package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findUserById(String id);

    Optional <UserEntity> findUserByEmail(String email);

    UserEntity saveUser(UserEntity userEntity);
}
