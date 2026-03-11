package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.domain.UserRepository;
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
    public Optional<UserEntity> findById(String id) {

        UserEntity userEntity = jpaUserRepository.findById(id).orElse(null);

        if(userEntity != null){
            return Optional.of(userEntity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {

        UserEntity userEntity = jpaUserRepository.findByEmail(email).orElse(null);

        if(userEntity != null){
            return Optional.of(userEntity);
        }

        return Optional.empty();
    }

    @Override
    public UserEntity save(UserEntity userEntity) {

       return  jpaUserRepository.save(userEntity);

    }
}
