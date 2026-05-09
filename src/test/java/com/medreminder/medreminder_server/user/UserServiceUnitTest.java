package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.application.dtos.user.*;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.users.UserServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void shouldCreateUser_thenSaveIt(){

        UUID userId = UUID.randomUUID();

//        Stub DATA...
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("test@mail.com",
                "test user", "12345678");

        User testUser = new User(userId.toString(),
                registerUserRequest.getEmail(), registerUserRequest.getName(),
                registerUserRequest.getPassword());

        testUser.addProfiles(new Profile(null, registerUserRequest.getName(), Relation.SELF, true));

        when(userRepository.saveUser(any(UserEntity.class)))
                .thenReturn(userMapper.toEntity(testUser));

        User user = userService.createUser(registerUserRequest);

        assertThat(user.getId()).isNotNull().isEqualTo(userId.toString());

        assertThat(user.getName()).isEqualTo("test user");

        assertThat(user.getHashPassword()).isEqualTo("12345678");


    }

    @Test
    void shouldUpdateUser_thenSaveIt(){

        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        User testUser = new User(userId.toString(), "test@email.com",
                "test user", "testhashpassword");
        Profile testProfile = new Profile(profileId.toString(),
                testUser.getName(),Relation.SELF, true);

        testUser.addProfiles(testProfile);

        UserEntity userEntity = userMapper.toEntity(testUser);

        UpdateUserCommand updateUserCommand = new UpdateUserCommand("updatetest@mail.com",
                null, "1992-07-27", "Male");

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userEntity));

        UserResponse response = userService.updateUser(userEntity.getId(), updateUserCommand);

        verify(userRepository).saveUser(any(UserEntity.class));

        assertThat(response.id()).isNotNull().isEqualTo(userId.toString());

        assertThat(response.email()).isEqualTo("updatetest@mail.com");

        assertThat(response.gender()).isNotNull().isEqualTo("Male");

        assertThat(response.dateOfBirth().getYear()).isEqualTo(1992);
    }

    @Test
    void shouldCreateProfile_thenSaveIt(){
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        User testUser = new User(userId.toString(), "test@email.com",
                "test user", "testhashpassword");

        ProfileRequest profileRequest = new ProfileRequest("James","BROTHER");

        Profile testProfile = new Profile(
                profileId.toString(),
                profileRequest.name(),
                Relation.valueOf(profileRequest.relation()),
                false);

        testUser.addProfiles(testProfile);

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userMapper.toEntity(testUser)));

        when(userRepository.saveUser(any(UserEntity.class)))
                .thenReturn(userMapper.toEntity(testUser));

        ProfileResponse response = userService.createProfile(testUser.getId(), profileRequest);

        assertThat(response.id()).isNotNull().isEqualTo(profileId.toString());
        assertThat(response.name()).isEqualTo(profileRequest.name());
        assertThat(response.relation()).isEqualTo("BROTHER");
    }


    @Test
    void shouldDeleteProfile_thenSaveIt(){
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        User testUser = new User(userId.toString(), "test@email.com",
                "test user", "testhashpassword");

        Profile testProfile = new Profile(profileId.toString(),
                "James",Relation.valueOf("BROTHER"), false);

        testUser.addProfiles(testProfile);

        UserEntity userEntity = userMapper.toEntity(testUser);

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userEntity));


        assertThat(userEntity.getProfiles().size()).isEqualTo(1);

        userService.deleteProfile(testUser.getId(), testProfile.getId());

        verify(userRepository).saveUser(any(UserEntity.class));

        assertThat(userEntity.getProfiles()).isEmpty();
    }


}
