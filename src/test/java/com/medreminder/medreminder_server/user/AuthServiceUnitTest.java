package com.medreminder.medreminder_server.user;

import com.medreminder.medreminder_server.application.dtos.user.ResetPasswordResponse;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.application.services.S3Service;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.models.users.UserProvider;
import com.medreminder.medreminder_server.domain.services.users.*;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
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

import java.util.List;
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
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper();
        JwtUtil jwtUtil = new JwtUtil(env);
        S3Service s3Service = new S3Service(null);
        UserService userService = new UserServiceImpl(userRepository, userMapper, subscriptionMapper,s3Service);
        TokenManager tokenManager = new TokenManager(jwtUtil, jpaRefreshTokenRepository,null);

        userMapper = new UserMapper();
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthServiceImpl(
                authenticationManager,
                userService,
                passwordEncoder,
                userMapper,
                userRepository,
                tokenManager);
    }

    @Test
    void shouldUpdatePassword_thenSaveIt(){
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        String hashedPassword = passwordEncoder.encode("testhashpassword");

        User testUser = new User(userId.toString(), "test@email.com",
                "test user", hashedPassword, UserProvider.LOCAL);
        Profile testProfile = new Profile(profileId.toString(),
                testUser.getName(), Relation.SELF, true);

        testUser.addProfiles(testProfile);

        UserEntity userEntity = userMapper.toEntity(testUser);

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(userEntity));

        when(jpaRefreshTokenRepository.findAllByUserIdAndRevokedFalse(any(String.class)))
                .thenReturn(List.of());

        ResetPasswordResponse response = authService
                .resetPassword(testUser.getId(),"testhashpassword", "testnewpassword");

        verify(userRepository).saveUser(any(UserEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("success");
        assertThat(response.message()).isNotEmpty();
    }
}
