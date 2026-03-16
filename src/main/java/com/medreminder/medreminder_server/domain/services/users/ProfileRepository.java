package com.medreminder.medreminder_server.domain.services.users;

import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;

import java.util.Optional;

public interface ProfileRepository {

    void saveProfile(ProfileEntity profileEntity);

    Optional<ProfileEntity> findProfileById(String id);
}
