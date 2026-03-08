package com.medreminder.medreminder_server.infrastructure.repository;

import com.medreminder.medreminder_server.infrastructure.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface JpaRefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {
}
