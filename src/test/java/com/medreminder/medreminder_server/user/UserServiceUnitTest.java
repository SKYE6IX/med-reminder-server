package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.UserRepository;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.UserServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    UserMapper userMapper = new UserMapper();

    @BeforeEach
    void setUp(){
        userService = new UserServiceImpl(userRepository);
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

        when(userRepository.save(any(UserEntity.class))).thenReturn(userMapper.toEntity(testUser));

        User user = userService.createUser(registerUserRequest);

        assertThat(user.getId()).isNotNull().isEqualTo(userId.toString());

        assertThat(user.getName()).isEqualTo("test user");

        assertThat(user.getHashPassword()).isEqualTo("12345678");


    }

    @Test
    void shouldUpdateUser_thenSaveIt(){

        UUID userId = UUID.randomUUID();

        RegisterUserRequest registerUserRequest = new RegisterUserRequest("test@mail.com",
                "test user", "12345678");

        User user = new User(userId.toString(), registerUserRequest.getEmail(),
                registerUserRequest.getName(), registerUserRequest.getPassword());

        user.addProfiles(new Profile(null, registerUserRequest.getName(), Relation.SELF, true));

        UpdateUserCommand updateUserCommand = new UpdateUserCommand("updatetest@mail.com",
                null, null, "Male");

        User updateUser = userService.updateUser(user, updateUserCommand);

        verify(userRepository, times(1)).save(any(UserEntity.class));

        assertThat(updateUser.getId()).isNotNull().isEqualTo(userId.toString());

        assertThat(updateUser.getEmail()).isEqualTo("updatetest@mail.com");

        assertThat(updateUser.getGender()).isNotNull();
    }

    @Test
    void shouldCreateProfile_thenSaveIt(){
        UUID userId = UUID.randomUUID();

        UUID profileId = UUID.randomUUID();

        User testUser = new User(userId.toString(), "test@email.com",
                "test user", "testhashpassword");

        ProfileRequest profileRequest = new ProfileRequest("James","BROTHER");

        Profile testProfile = new Profile(profileId.toString(),
                profileRequest.fullName(),Relation.valueOf(profileRequest.relation()), false);

        testUser.addProfiles(testProfile);

        when(userRepository.save(any(UserEntity.class))).thenReturn(userMapper.toEntity(testUser));

        Profile profile = userService.createProfile(testUser, profileRequest);

        assertThat(profile.getId()).isNotNull().isEqualTo(profileId.toString());

        assertThat(profile.getName()).isEqualTo(profileRequest.fullName());

        assertThat(profile.getRelation()).isEqualTo(Relation.BROTHER);
    }
}
