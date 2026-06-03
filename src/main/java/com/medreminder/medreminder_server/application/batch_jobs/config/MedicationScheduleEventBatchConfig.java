package com.medreminder.medreminder_server.application.batch_jobs.config;

import com.medreminder.medreminder_server.application.batch_jobs.listener.MedicationScheduleEventJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.processing.MedicationScheduleEventProcessor;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaMedicationScheduleRepo;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

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
                                            JdbcCursorItemReader<MedicationScheduleEntity> reader,
                                            MedicationScheduleEventProcessor processor,
                                            ItemWriter<MedicationScheduleEntity> writer
                                            ) {
        return new StepBuilder("medication_schedule_event_step",jobRepository)
                .<MedicationScheduleEntity, MedicationScheduleEntity>chunk(50)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<MedicationScheduleEntity> reader(DataSource dataSource){
        return new JdbcCursorItemReaderBuilder<MedicationScheduleEntity>()
                .name("medication_schedule_reader")
                .dataSource(dataSource)
                .sql("""
                        SELECT ms.*
                        FROM medication_schedules ms
                        JOIN medication_profiles mp ON mp.id = ms.medication_profile_id
                        WHERE mp.is_active
                            AND ms.last_expanded_until = CURRENT_DATE + INTERVAL '1 day';
                        """)
                .rowMapper(new BeanPropertyRowMapper<>(MedicationScheduleEntity.class))
                .build();
    }

    @Bean
    public MedicationScheduleEventProcessor processor(ScheduleEventService scheduleEventService,
                                                      MedicationMapper medicationMapper){
        return new MedicationScheduleEventProcessor(scheduleEventService, medicationMapper);
    }

    @Bean
    public ItemWriter<MedicationScheduleEntity> writer(JpaMedicationScheduleRepo medicationScheduleRepo){
        return medicationScheduleRepo::saveAll;
    }
}