package com.medreminder.medreminder_server.application.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.listener.DowngradePlanJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.processing.DowngradePlanProcessor;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.repository.subscription.JpaSubscriptionRepository;
import jakarta.persistence.EntityManager;
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
public class DowngradePlanBatchConfig {

    @Bean
    public Job downgradePlanJob(JobRepository jobRepository,
                                Step downgradePlanStep,
                                DowngradePlanJobListener downgradePlanJobListener){
        return new JobBuilder("downgrade_plan_job", jobRepository)
                .listener(downgradePlanJobListener)
                .start(downgradePlanStep)
                .build();
    }

    @Bean
    public Step downgradePlanStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  JpaCursorItemReader<SubscriptionPeriodEntity> downgradePlanReader,
                                  DowngradePlanProcessor downgradePlanProcessor,
                                  ItemWriter<SubscriptionEntity> downgradePlanWriter
                                  ) {
        return new StepBuilder("downgrade_plan_step", jobRepository)
                .<SubscriptionPeriodEntity, SubscriptionEntity>chunk(50)
                .transactionManager(transactionManager)
                .reader(downgradePlanReader)
                .processor(downgradePlanProcessor)
                .writer(downgradePlanWriter)
                .build();
    }

    @Bean
    public JpaCursorItemReader<SubscriptionPeriodEntity> downgradePlanReader(EntityManagerFactory entityManagerFactory){
        LocalDate now = LocalDate.now();
        LocalDateTime startOfYesterday = now.minusDays(1).atStartOfDay();
        LocalDateTime startOfToday = now.atStartOfDay();

        return new JpaCursorItemReaderBuilder<SubscriptionPeriodEntity>()
                .name("downgrade_plan_reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                    SELECT sp
                    FROM SUBSCRIPTION_PERIODS sp
                    JOIN sp.subscription s
                    WHERE s.status = 'CANCELED'
                        AND sp.endTime >= :startOfYesterday
                        AND sp.endTime < :startOfToday
                        AND sp.status = 'ACTIVE'
                    """)
                .parameterValues(Map.of(
                        "startOfYesterday", startOfYesterday,
                        "startOfToday", startOfToday
                ))
                .build();
    }

    @Bean
    public DowngradePlanProcessor downgradePlanProcessor(SubscriptionMapper subscriptionMapper){
        return new DowngradePlanProcessor(subscriptionMapper);
    }

    @Bean
    public ItemWriter<SubscriptionEntity> downgradePlanWriter(JpaSubscriptionRepository subscriptionRepository){
        return (items) -> {
          subscriptionRepository.saveAll(items.getItems());
        };
    }
}
