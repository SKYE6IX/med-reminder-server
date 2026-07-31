package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends BaseJpaRepository<UserEntity, String> {
    Optional <UserEntity> findByEmail(String email);
    Optional <UserEntity> findByProviderId(String providerId);
}
