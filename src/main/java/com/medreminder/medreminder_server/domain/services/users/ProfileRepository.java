package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;

import java.util.Optional;

public interface ProfileRepository {
    Optional<ProfileEntity> findProfileById(String id);

    ProfileEntity saveProfile(ProfileEntity profileEntity);
}
