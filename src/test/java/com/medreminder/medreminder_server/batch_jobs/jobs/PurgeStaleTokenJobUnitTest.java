package com.medreminder.medreminder_server.batch_jobs.jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.PurgeStaleTokenBatchConfig;
import com.medreminder.medreminder_server.infrastructure.entity.users.RefreshTokenEntity;
import com.medreminder.medreminder_server.infrastructure.repository.users.JpaRefreshTokenRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBatchTest
@SpringJUnitConfig(classes = {
        TestConfig.class,
        PurgeStaleTokenBatchConfig.class
})
@ActiveProfiles("test")
public class PurgeStaleTokenJobUnitTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    @Qualifier("purgeStaleTokenJob")
    private Job purgeStaleTokenJob;

    @Autowired
    private JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void jobShouldProcessOnlyEligibleAndCompleteSuccessfully() throws Exception {

        List<RefreshTokenEntity> refreshTokenEntities = IntStream.range(0, 10)
                .mapToObj(i -> new RefreshTokenEntity(
                        null,
                        i + "hash_token",
                        LocalDateTime.now(ZoneId.of("Europe/Moscow")).minusWeeks(i+1),
                        true,
                        null
                ))
                .toList();

        jpaRefreshTokenRepository.saveAll(refreshTokenEntities);

        jobOperatorTestUtils.setJob(purgeStaleTokenJob);

        JobExecution execution = jobOperatorTestUtils.startJob(
                new JobParametersBuilder()
                        .addLocalDate("runDate", LocalDate.now())
                        .toJobParameters()
        );

        StepExecution stepExecution = execution.getStepExecutions().iterator().next();
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(stepExecution.getReadCount()).isGreaterThan(5);
        assertThat(stepExecution.getWriteCount()).isGreaterThan(5);
    }
}
