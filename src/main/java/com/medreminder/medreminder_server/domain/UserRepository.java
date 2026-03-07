package com.medreminder.medreminder_server.domain;

import com.medreminder.medreminder_server.infrastructure.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findById(String id);

    Optional <UserEntity> findUserByEmail(String email);

    UserEntity save(UserEntity userEntity);

}
