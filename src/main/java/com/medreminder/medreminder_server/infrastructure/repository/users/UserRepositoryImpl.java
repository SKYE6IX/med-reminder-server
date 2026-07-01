package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<UserEntity> findUserById(String id) {
        return  jpaUserRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
       return jpaUserRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findUserByProviderId(String providerId) {
        return jpaUserRepository.findByProviderId(providerId);
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
       return  jpaUserRepository.save(userEntity);
    }

    @Override
    public void deleteUser(String userId) {
        jpaUserRepository.deleteById(userId);
    }
}
