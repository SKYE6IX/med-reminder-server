package com.medreminder.medreminder_server.application.batch_jobs.config;

import com.medreminder.medreminder_server.application.batch_jobs.listener.PurgeStaleTokenJobListener;
import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class PurgeStaleTokenBatchConfig {

    @Bean
    public Job purgeStaleTokenJob(JobRepository jobRepository,
                                  Step purgeStaleTokenStep,
                                  PurgeStaleTokenJobListener purgeStaleTokenJobListener){
        return new JobBuilder("purge_stale_token_job", jobRepository)
                .listener(purgeStaleTokenJobListener)
                .start(purgeStaleTokenStep)
                .build();
    }

    @Bean
    public Step purgeStaleTokenStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    JpaCursorItemReader<RefreshTokenEntity> purgeStaleTokenReader,
                                    ItemWriter<RefreshTokenEntity> purgeStaleTokenWriter
                                    ){
        return new StepBuilder("purge_stale_token_step", jobRepository)
                .<RefreshTokenEntity,RefreshTokenEntity>chunk(50)
                .transactionManager(transactionManager)
                .reader(purgeStaleTokenReader)
                .writer(purgeStaleTokenWriter)
                .build();
    }

    @Bean
    public JpaCursorItemReader<RefreshTokenEntity> purgeStaleTokenReader(
            EntityManagerFactory entityManagerFactory){
        return new JpaCursorItemReaderBuilder<RefreshTokenEntity>()
                .name("purge_stale_token_reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT rt
                        FROM REFRESH_TOKENS rt
                        WHERE rt.revoked = true
                        AND rt.expiredAt < :cutoffDate
                        """)
                .parameterValues(Map.of(
                        "cutoffDate",LocalDateTime.now(ZoneId.of("Europe/Moscow")).minusWeeks(2)
                ))
                .build();
    }

    @Bean
    public ItemWriter<RefreshTokenEntity> purgeStaleTokenWriter(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        return (items)->
                jpaRefreshTokenRepository.deleteAll(items.getItems());
    }
}
