package com.medreminder.medreminder_server;

import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.UserRepository;
import com.medreminder.medreminder_server.domain.model.User;
import com.medreminder.medreminder_server.domain.service.UserServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
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

    @BeforeEach
    void setUp(){
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldCreateUser_thenSaveIt(){

        UUID userId = UUID.randomUUID();

        RegisterUserRequest registerUserRequest = new RegisterUserRequest("test@mail.com",
                "test user", "12345678");

        UserEntity userEntity = new UserEntity(userId.toString(),
                registerUserRequest.getEmail(),registerUserRequest.getName(),
                registerUserRequest.getPassword());

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

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

//        Stub user entity data
        UserEntity userEntity = new UserEntity(userId.toString(),
                registerUserRequest.getEmail(), registerUserRequest.getName(),
                registerUserRequest.getPassword());

        UpdateUserCommand updateUserCommand = new UpdateUserCommand("updatetest@mail.com",
                null, null, null, "Male");

        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(userEntity));

        User updateUser = userService.updateUser(userEntity.getId(), updateUserCommand);

        assertThat(updateUser.getId()).isNotNull().isEqualTo(userId.toString());

        assertThat(updateUser.getEmail()).isEqualTo("updatetest@mail.com");

        assertThat(updateUser.getGender()).isNotNull();
    }
}
