package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaRefreshTokenRepository extends BaseJpaRepository<RefreshTokenEntity, Long> {

    @Query("select rt from REFRESH_TOKENS rt left join fetch rt.user where rt.hashToken = :hashToken")
    RefreshTokenEntity findByHashToken(@Param("hashToken") String hashToken);

//    We are using findAll, beacuse there case where for some reason use has got
//    more than one refreshtoken. probaly becasue they sign in from another mobile.
    List<RefreshTokenEntity> findAllByUserIdAndRevokedFalse(String userId);
}
