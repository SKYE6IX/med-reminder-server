package com.medreminder.medreminder_server.infrastructure.repository;

import com.medreminder.medreminder_server.infrastructure.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {

    RefreshTokenEntity findByHashToken(String hashToken);

    Optional<RefreshTokenEntity> findByUserIdAndRevokedFalse(String userId);
}
