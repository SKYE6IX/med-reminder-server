package com.medreminder.medreminder_server.application.config;


import com.medreminder.medreminder_server.application.security.AppleTokenVerifier;
import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.application.services.S3Service;
import com.medreminder.medreminder_server.domain.services.UseCase;
import com.medreminder.medreminder_server.domain.services.medications.*;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionService;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionServiceImpl;
import com.medreminder.medreminder_server.domain.services.users.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

@Configuration
@EnableTransactionManagement
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
                            TransactionInterceptor txInterceptor ) {

        UserService userService = new UserServiceImpl(userRepository, userMapper,subscriptionMapper,s3Service);
        return createProxyFactory(userService,UserService.class, txInterceptor);
    }

    @Bean
    public AuthService authService(AuthenticationManager authenticationManager,
                            UserService userService,
                            PasswordEncoder passwordEncoder,
                            UserMapper userMapper,
                            UserRepository userRepository,
                            TokenManager tokenManager,
                            TransactionInterceptor txInterceptor) {

        AuthService authService = new AuthServiceImpl(
                authenticationManager,
                userService,
                passwordEncoder,
                userMapper,
                userRepository,
                tokenManager
        );
        return createProxyFactory(authService, AuthService.class,txInterceptor);
    }

    @Bean
    public MedicationProfileService medicationService(MedicationRepository medicationRepository,
                                               ProfileRepository profileRepository,
                                               MedicationMapper medicationMapper,
                                               ScheduleEventService scheduleEventService,
                                               UserMapper userMapper,
                                               TransactionInterceptor txInterceptor) {
        MedicationProfileService medicationProfileService = new MedicationProfileServiceImpl(
                medicationRepository,
                profileRepository,
                medicationMapper,
                scheduleEventService,
                userMapper);

        return createProxyFactory(medicationProfileService, MedicationProfileService.class, txInterceptor);
    }

    @Bean
    public ScheduleEventService scheduleEventService(MedicationRepository medicationRepository,
                                              MedicationMapper medicationMapper,
                                              TransactionInterceptor txInterceptor) {
        ScheduleEventService scheduleEventService = new ScheduleEventServiceImpl(medicationRepository,
                medicationMapper);

        return createProxyFactory(scheduleEventService, ScheduleEventService.class, txInterceptor);
    }

    @Bean
    public SubscriptionService subscriptionService(SubscriptionRepository subscriptionRepository,
                                            UserRepository userRepository,
                                            PaymentService paymentService,
                                            SubscriptionMapper subscriptionMapper,
                                            TransactionInterceptor txInterceptor){
        SubscriptionService subscriptionService = new
                SubscriptionServiceImpl(subscriptionRepository,userRepository,paymentService,subscriptionMapper);

        return createProxyFactory(subscriptionService, SubscriptionService.class, txInterceptor);
    }

    @Bean
    TokenManager tokenManager(JwtUtil jwtUtil,
                              JpaRefreshTokenRepository jpaRefreshTokenRepository,
                              AppleTokenVerifier appleTokenVerifier) {
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
    public TransactionInterceptor txInterceptor(TransactionManager txManager) {
        TransactionAttributeSource source = getAttributeSource();
        return new TransactionInterceptor(txManager, source);
    }

    private TransactionAttributeSource getAttributeSource() {
        return new TransactionAttributeSource() {
            private final RuleBasedTransactionAttribute readWrite = buildReadWrite();
            private final RuleBasedTransactionAttribute readOnly  = buildReadOnly();

            @Override
            public @Nullable TransactionAttribute getTransactionAttribute(@NonNull Method method,
                                                                          @Nullable Class<?> targetClass) {
                if (targetClass != null && !UseCase.class.isAssignableFrom(targetClass)) return null;

                String methodName = method.getName();

                if (methodName.startsWith("get") || methodName.startsWith("find")) return readOnly;

                return readWrite;
            }
        };
    }

    private RuleBasedTransactionAttribute buildReadWrite() {
        RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
        attr.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        attr.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));
        return attr;
    }

    private RuleBasedTransactionAttribute buildReadOnly() {
        RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
        attr.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        attr.setReadOnly(true);
        return attr;
    }

    private <T> T createProxyFactory(T target, Class<T> serviceInterface,
                                     TransactionInterceptor txInterceptor ) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(txInterceptor);
        proxyFactory.addInterface(serviceInterface);
        return serviceInterface.cast(proxyFactory.getProxy());
    }
}
