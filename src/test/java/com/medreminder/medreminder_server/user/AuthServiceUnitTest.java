package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.users.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @Mock
    private Environment env;

    private PasswordEncoder passwordEncoder;

    private AuthService authService;
    private UserMapper userMapper;

    @BeforeEach
    void setUp(){
        when(env.getProperty("med.reminder.jwt.key"))
                .thenReturn("ZUDyDWpzCerpPDJwVkkmOquSPBi2O3hi/JcsvrEP/I01Pf2cfpKqqMhj+tJjY1CgwxSBmg+5xSITdNsWAT8TIA==");

        JwtUtil jwtUtil = new JwtUtil(env);
        UserService userService = new UserServiceImpl(userRepository, userMapper);

        userMapper = new UserMapper();
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(authenticationManager,
                userService, passwordEncoder,
                jwtUtil, userMapper, jpaRefreshTokenRepository
                ,userRepository);
    }

    @Test
    void shouldUpdatePassword_thenSaveIt(){
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        String hashedPassword = passwordEncoder.encode("testhashpassword");

        User testUser = new User(userId.toString(), "test@email.com",
                "test user", hashedPassword);
        Profile testProfile = new Profile(profileId.toString(),
                testUser.getName(), Relation.SELF, true);

        testUser.addProfiles(testProfile);

        UserEntity userEntity = userMapper.toEntity(testUser);

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userEntity));

        when(jpaRefreshTokenRepository.findByUserIdAndRevokedFalse(any(String.class)))
                .thenReturn(Optional.empty());

        AuthResponse response = authService
                .resetPassword(testUser.getId(),"testhashpassword", "testnewpassword");

        verify(userRepository).saveUser(any(UserEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userEntity.getId());
        assertThat(response.email()).isEqualTo(testUser.getEmail());
        assertThat(response.accessToken()).isNotEmpty();
    }
}
