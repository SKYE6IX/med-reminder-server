package com.medreminder.medreminder_server.infrastructure.repository.users;

import com.medreminder.medreminder_server.domain.services.users.UserRepository;
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

        UserEntity userEntity = jpaUserRepository.findById(id).orElse(null);

        if(userEntity != null){

            System.out.println(userEntity.getProfiles());

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
    public UserEntity saveUser(UserEntity userEntity) {

       return  jpaUserRepository.save(userEntity);

    }
}
