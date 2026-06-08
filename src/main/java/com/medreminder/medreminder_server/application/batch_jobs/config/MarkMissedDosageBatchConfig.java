package com.medreminder.medreminder_server.application.batch_jobs.config;

import com.medreminder.medreminder_server.application.batch_jobs.listener.MarkMissedDosageJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.processing.MarkMissedDosageProcessor;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaScheduleEventRepo;
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
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class MarkMissedDosageBatchConfig {
    @Bean
    public Job markMissedDosageJob(JobRepository jobRepository,
                                   Step markMissedDosageStep,
                                   MarkMissedDosageJobListener markMissedDosageJobListener){
        return new JobBuilder("mark_missed_dosage_job", jobRepository)
                .listener(markMissedDosageJobListener)
                .start(markMissedDosageStep)
                .build();
    }

    @Bean
    public Step markMissedDosageStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     JpaCursorItemReader<ScheduleEventEntity> markMissedDosageReader,
                                     MarkMissedDosageProcessor markMissedDosageProcessor,
                                     ItemWriter<ScheduleEventEntity> markMissedDosageWriter){
        return new StepBuilder("mark_missed_dosage_step", jobRepository)
                .<ScheduleEventEntity,ScheduleEventEntity>chunk(50)
                .transactionManager(transactionManager)
                .reader(markMissedDosageReader)
                .processor(markMissedDosageProcessor)
                .writer(markMissedDosageWriter)
                .build();
    }

    @Bean
    public JpaCursorItemReader<ScheduleEventEntity> markMissedDosageReader(
            EntityManagerFactory entityManagerFactory) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfYesterday = today.minusDays(1).atStartOfDay();

    return new JpaCursorItemReaderBuilder<ScheduleEventEntity>()
            .name("mark_missed_dosage_reader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("""
                    SELECT se
                    FROM SCHEDULE_EVENTS se
                    WHERE se.status = 'PENDING'
                        AND se.scheduleAt >= :startOfYesterday
                        AND se.scheduleAt < :startOfToday
                    """)
            .parameterValues(Map.of(
                    "startOfYesterday", startOfYesterday,
                    "startOfToday", startOfToday
            ))
            .build();
    }

    @Bean
    public MarkMissedDosageProcessor markMissedDosageProcessor(MedicationMapper medicationMapper) {
        return new MarkMissedDosageProcessor(medicationMapper);
    }

    @Bean
    public ItemWriter<ScheduleEventEntity> markMissedDosageWriter(JpaScheduleEventRepo scheduleEventRepo) {
        return (items)->
                scheduleEventRepo.saveAll(items.getItems());
    }
}