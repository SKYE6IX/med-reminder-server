package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;

import java.util.Optional;

public interface UserRepository {

    UserEntity saveUser(UserEntity userEntity);

    void deleteUser(String userId);

    Optional<UserEntity> findUserById(String id);

    Optional <UserEntity> findUserByEmail(String email);
}
