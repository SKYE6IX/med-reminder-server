package com.medreminder.medreminder_server.application.config;


import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.MedicationService;
import com.medreminder.medreminder_server.domain.services.medications.MedicationServiceImpl;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.domain.services.users.UserService;
import com.medreminder.medreminder_server.domain.services.users.UserServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    MedicationService medicationService(MedicationRepository medicationRepository,
                                        ProfileRepository profileRepository,
                                        MedicationMapper medicationMapper,
                                        ScheduleEventService scheduleEventService) {

        return new MedicationServiceImpl(medicationRepository,
                profileRepository,
                medicationMapper, scheduleEventService);
    }
}
