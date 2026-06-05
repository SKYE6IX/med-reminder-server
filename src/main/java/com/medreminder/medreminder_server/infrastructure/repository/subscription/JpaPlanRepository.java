package com.medreminder.medreminder_server.infrastructure.repository.subscription;

import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface JpaPlanRepository extends BaseJpaRepository<PlanEntity, String> {
    Optional<PlanEntity> findByUserId(String userId);
}
