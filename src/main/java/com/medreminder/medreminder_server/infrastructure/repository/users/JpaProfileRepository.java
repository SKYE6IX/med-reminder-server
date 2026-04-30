package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

import java.util.List;

public interface JpaProfileRepository extends BaseJpaRepository<ProfileEntity, String> {
}
