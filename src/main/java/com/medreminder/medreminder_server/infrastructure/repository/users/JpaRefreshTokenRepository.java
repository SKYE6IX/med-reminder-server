package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

import java.util.Optional;

public interface JpaRefreshTokenRepository extends BaseJpaRepository<RefreshTokenEntity, Long> {

    RefreshTokenEntity findByHashToken(String hashToken);

    Optional<RefreshTokenEntity> findByUserIdAndRevokedFalse(String userId);
}
