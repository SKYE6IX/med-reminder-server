package com.medreminder.medreminder_server.application.config;


import com.medreminder.medreminder_server.domain.UserRepository;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }
}
