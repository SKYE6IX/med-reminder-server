package com.medreminder.medreminder_server.application.config;


import com.medreminder.medreminder_server.application.security.AppleAuth;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.application.services.EmailService;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.application.services.S3Service;
import com.medreminder.medreminder_server.domain.services.users.TokenManager;
import com.medreminder.medreminder_server.domain.services.medications.*;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionService;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionServiceImpl;
import com.medreminder.medreminder_server.domain.services.users.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.interceptor.*;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class AppConfig {
    private final Environment env;

    @Autowired
    public AppConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public UserService userService(UserRepository userRepository,
                            UserMapper userMapper,
                            SubscriptionMapper subscriptionMapper,
                            S3Service s3Service,
                            AppleAuth appleAuth) {

        return new UserServiceImpl(userRepository,
                userMapper, subscriptionMapper, s3Service, appleAuth);
    }

    @Bean
    public AuthService authService(AuthenticationManager authenticationManager,
                            UserService userService,
                            PasswordEncoder passwordEncoder,
                            UserMapper userMapper,
                            UserRepository userRepository,
                            TokenManager tokenManager,
                            EmailService emailService) {

        return new AuthServiceImpl(
                authenticationManager,
                userService,
                passwordEncoder,
                userMapper,
                userRepository,
                tokenManager,
                emailService
        );
    }

    @Bean
    public MedicationProfileService medicationService(MedicationRepository medicationRepository,
                                               ProfileRepository profileRepository,
                                               MedicationMapper medicationMapper,
                                               ScheduleEventService scheduleEventService,
                                               UserMapper userMapper) {
        return new MedicationProfileServiceImpl(
                medicationRepository,
                profileRepository,
                medicationMapper,
                scheduleEventService,
                userMapper);
    }

    @Bean
    public ScheduleEventService scheduleEventService(MedicationRepository medicationRepository,
                                              MedicationMapper medicationMapper) {
        return new ScheduleEventServiceImpl(medicationRepository,
                medicationMapper);
    }

    @Bean
    public SubscriptionService subscriptionService(SubscriptionRepository subscriptionRepository,
                                            UserRepository userRepository,
                                            PaymentService paymentService,
                                            SubscriptionMapper subscriptionMapper){

        return new SubscriptionServiceImpl(subscriptionRepository,
                userRepository, paymentService, subscriptionMapper);
    }

    @Bean
    TokenManager tokenManager(JwtUtil jwtUtil,
                              JpaRefreshTokenRepository jpaRefreshTokenRepository,
                              AppleAuth appleTokenVerifier) {
        return new TokenManager(jwtUtil, jpaRefreshTokenRepository, appleTokenVerifier);
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(env.getProperty("yc.s3.region")))
                .endpointOverride(URI.create(env.getProperty("yc.s3.private.endpoint")))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        env.getProperty("yc.s3.key.id"),env.getProperty("yc.s3.secret.key"))
                        )
                )
                .build();
    }

    @Bean
    public RestClient appleRestClient() {
        return RestClient.builder()
                .baseUrl("https://appleid.apple.com")
                .build();
    }
}
