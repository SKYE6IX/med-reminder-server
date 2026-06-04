package com.medreminder.medreminder_server.application.batch_jobs.config;

import com.medreminder.medreminder_server.application.batch_jobs.listener.MedicationScheduleEventJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.processing.MedicationScheduleEventProcessor;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaMedicationScheduleRepo;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class MedicationScheduleEventBatchConfig {

    @Bean
    public Job medicationScheduleEventJob(JobRepository jobRepository,
                                          Step medicationScheduleEventStep,
                                          MedicationScheduleEventJobListener listener){
        return new JobBuilder("medicationScheduleEventJob", jobRepository)
                .listener(listener)
                .start(medicationScheduleEventStep)
                .build();
    }

    @Bean
    public Step medicationScheduleEventStep(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager,
                                            JpaCursorItemReader<MedicationScheduleEntity> reader,
                                            MedicationScheduleEventProcessor processor,
                                            ItemWriter<MedicationScheduleEntity> writer
                                            ) {
        return new StepBuilder("medication_schedule_event_step", jobRepository)
                .<MedicationScheduleEntity, MedicationScheduleEntity>chunk(50)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public JpaCursorItemReader<MedicationScheduleEntity> reader(EntityManagerFactory entityManagerFactory){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end   = tomorrow.plusDays(1).atStartOfDay();

        return new JpaCursorItemReaderBuilder<MedicationScheduleEntity>()
                .name("medication_schedule_reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                    SELECT ms
                    FROM MEDICATION_SCHEDULES ms
                    JOIN ms.medicationProfile mp
                    WHERE mp.isActive = true
                        AND ms.lastExpandedUntil >= :start
                        AND ms.lastExpandedUntil < :end
                    """)
                .parameterValues(Map.of(
                        "start", start,
                        "end", end
                ))
                .build();
    }

    @Bean
    public MedicationScheduleEventProcessor processor(ScheduleEventService scheduleEventService,
                                                      MedicationMapper medicationMapper){
        return new MedicationScheduleEventProcessor(scheduleEventService, medicationMapper);
    }

    @Bean
    public ItemWriter<MedicationScheduleEntity> writer(JpaMedicationScheduleRepo medicationScheduleRepo){
        return (items) -> {
            medicationScheduleRepo.saveAll(items.getItems());
        };
    }
}