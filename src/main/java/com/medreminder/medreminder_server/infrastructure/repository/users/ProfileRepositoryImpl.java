package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final JpaProfileRepository jpaProfileRepository;

    public ProfileRepositoryImpl( JpaProfileRepository jpaProfileRepository) {
        this.jpaProfileRepository = jpaProfileRepository;
    }

    @Override
    public Optional<ProfileEntity> findProfileById(String id) {

        ProfileEntity profileEntity = jpaProfileRepository.findById(id)
                .orElse(null);

        if(profileEntity != null){
            return Optional.of(profileEntity);
        }
        return Optional.empty();
    }

    @Override
    public ProfileEntity saveProfile(ProfileEntity profileEntity) {
        return jpaProfileRepository.save(profileEntity);
    }
}
