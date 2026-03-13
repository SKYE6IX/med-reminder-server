package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaRefreshTokenRepository extends BaseJpaRepository<RefreshTokenEntity, Long> {

    @Query("select rt from REFRESH_TOKEN rt left join fetch rt.user where rt.hashToken = :hashToken")
    RefreshTokenEntity findByHashToken(@Param("hashToken") String hashToken);

    Optional<RefreshTokenEntity> findByUserIdAndRevokedFalse(String userId);
}
