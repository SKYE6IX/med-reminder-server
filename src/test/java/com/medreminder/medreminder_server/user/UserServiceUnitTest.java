package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.application.services.S3Service;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.users.UserServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;
    private UserMapper userMapper;

    @BeforeEach
    void setUp(){
        userMapper = new UserMapper();
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper();
        S3Service s3Service = new S3Service(null);
        userService = new UserServiceImpl(userRepository, userMapper, subscriptionMapper, s3Service);
    }

    @Test
    void shouldCreateUser_thenSaveIt(){
        User snubUser = UserStubData
                .createStubUserWithId("test@mail.com","test user", "12345678");


        when(userRepository.saveUser(any(UserEntity.class)))
                .thenReturn(userMapper.toEntity(snubUser));

        UserEntity user = userService.createUser(
                new RegisterUserRequest("test@mail.com","test user", "12345678")
                ,UserProvider.LOCAL);

        assertThat(user.getId()).isNotNull().isEqualTo(snubUser.getId());

        assertThat(user.getName()).isEqualTo("test user");

        assertThat(user.getHashPassword()).isEqualTo("12345678");
    }

    @Test
    void shouldUpdateUser_thenSaveIt(){
        User snubUser = UserStubData
                .createStubUserWithId("test@mail.com","test user", "12345678");

        UpdateUserCommand updateUserCommand = new UpdateUserCommand("updatetest@mail.com",
                null, "27.07.1992", "Male");

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userMapper.toEntity(snubUser)));

        UserResponse response = userService.updateUser(snubUser.getId(), updateUserCommand);

        verify(userRepository).saveUser(any(UserEntity.class));

        assertThat(response.id()).isNotNull().isEqualTo(snubUser.getId());

        assertThat(response.email()).isEqualTo("updatetest@mail.com");

        assertThat(response.gender()).isNotNull().isEqualTo("Male");

        assertThat(response.dateOfBirth().getYear()).isEqualTo(1992);
    }

    @Test
    void shouldCreateProfile_thenSaveIt(){
        User snubUser = UserStubData
                .createStubUserWithId("test@mail.com","test user", "12345678");
        Profile snubProfile = UserStubData
                .createStubProfileWithId("John", Relation.BROTHER.toString(),false);

        snubUser.addProfiles(snubProfile);

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userMapper.toEntity(snubUser)));

        when(userRepository.saveUser(any(UserEntity.class)))
                .thenReturn(userMapper.toEntity(snubUser));

        ProfileResponse response = userService.createProfile(snubProfile.getId(),
                new ProfileRequest("John",Relation.BROTHER.toString()));

        assertThat(response.id()).isNotNull().isEqualTo(snubProfile.getId());
        assertThat(response.name()).isEqualTo(snubProfile.getName());
        assertThat(response.relation()).isEqualTo("BROTHER");
    }


    @Test
    void shouldDeleteProfile_thenSaveIt(){
        User snubUser = UserStubData
                .createStubUserWithId("test@mail.com","test user", "12345678");

        Profile snubProfile = UserStubData
                .createStubProfileWithId("John", Relation.BROTHER.toString(),false);

        snubUser.addProfiles(snubProfile);

        UserEntity userEntity = userMapper.toEntity(snubUser);

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userEntity));

        assertThat(userEntity.getProfiles().size()).isEqualTo(2);

        userService.deleteProfile(userEntity.getId(), snubProfile.getId());

        verify(userRepository).saveUser(any(UserEntity.class));

        assertThat(userEntity.getProfiles().size()).isEqualTo(1);
    }


}
