package com.medreminder.medreminder_server.infrastructure.repository;

import com.medreminder.medreminder_server.infrastructure.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUserRepository extends CrudRepository<UserEntity, String> {

    Optional <UserEntity> findByEmail(String email);
}
