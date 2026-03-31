package com.medreminder.medreminder_server.application.config;


import com.medreminder.medreminder_server.application.security.JwtUtil;
import com.medreminder.medreminder_server.domain.services.UseCase;
import com.medreminder.medreminder_server.domain.services.medications.*;
import com.medreminder.medreminder_server.domain.services.users.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public TransactionInterceptor txInterceptor(TransactionManager txManager) {
        TransactionAttributeSource source = getAttributeSource();
        return new TransactionInterceptor(txManager, source);
    }

    @Bean
    UserService userService(UserRepository userRepository,
                            UserMapper userMapper,
                            TransactionInterceptor txInterceptor ) {

        UserService userService = new UserServiceImpl(userRepository, userMapper);

        return createProxyFactory(userService,UserService.class,txInterceptor);
    }

    @Bean
    AuthService authService(AuthenticationManager authenticationManager,
                            UserService userService,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil,
                            UserMapper userMapper,
                            JpaRefreshTokenRepository jpaRefreshTokenRepository,
                            UserRepository userRepository,
                            TransactionInterceptor txInterceptor) {

        AuthService authService = new AuthServiceImpl(
                authenticationManager,
                userService, passwordEncoder,
                jwtUtil, userMapper,
                jpaRefreshTokenRepository, userRepository
        );

        return createProxyFactory(authService, AuthService.class,txInterceptor);
    }

    @Bean
    MedicationService medicationService(MedicationRepository medicationRepository,
                                        ProfileRepository profileRepository,
                                        MedicationMapper medicationMapper,
                                        ScheduleEventService scheduleEventService,
                                        UserMapper userMapper,
                                        TransactionInterceptor txInterceptor) {

        MedicationService medicationService = new MedicationServiceImpl(
                medicationRepository,
                profileRepository,
                medicationMapper,
                scheduleEventService,
                userMapper);
        return createProxyFactory(medicationService, MedicationService.class, txInterceptor);
    }

    @Bean
    ScheduleEventService scheduleEventService(MedicationRepository medicationRepository,
                                              MedicationMapper medicationMapper,
                                              TransactionInterceptor txInterceptor) {

        ScheduleEventService scheduleEventService = new ScheduleEventServiceImpl(medicationRepository,
                medicationMapper);

        return createProxyFactory(scheduleEventService, ScheduleEventService.class, txInterceptor);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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
