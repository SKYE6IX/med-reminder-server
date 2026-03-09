package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends BaseJpaRepository<UserEntity, String> {

    Optional <UserEntity> findByEmail(String email);
}
