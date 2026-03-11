package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends BaseJpaRepository<UserEntity, String> {

    @Query("select u from USERS u left join fetch u.profiles where u.email = :email")
    Optional <UserEntity> findByEmail(@Param("email") String email);
}
