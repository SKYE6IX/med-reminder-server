package com.medreminder.medreminder_server.batch_jobs.jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.DowngradePlanBatchConfig;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionStatus;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.subscription.SubscriptionServiceStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBatchTest
@SpringJUnitConfig(classes = {
        TestConfig.class,
        DowngradePlanBatchConfig.class
})
@ActiveProfiles("test")
public class DowngradePlanJobUnitTest {

    @Autowired
    private  UserMapper userMapper;

    @Autowired
    private  SubscriptionMapper subscriptionMapper;

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    private Job downgradePlanJob;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void jobShouldProcessOnlyEligibleAndCompleteSuccessfully() throws Exception {
        User stubUser = UserStubData
                .createUser(null,"test@mai.com","test user", null);
        var plan = SubscriptionServiceStubFactory
                .createPlan(null);
        plan.toProPlan();
        var stubUserEntity = userMapper.toEntity(stubUser);
        stubUserEntity.setPlan(subscriptionMapper.toEntity(plan,stubUserEntity));

        var savedUser = userRepository.saveUser(stubUserEntity);

        var stubSubscriptionEntity = subscriptionMapper
                .toEntity(SubscriptionServiceStubFactory
                        .createSubscription(null), savedUser);

        var stubPeriod = SubscriptionServiceStubFactory
                .createSubscriptionPeriod(null,
                        LocalDateTime.now().minusYears(1), LocalDateTime.now().minusDays(1));

        var stubPeriodEntity = subscriptionMapper
                .toEntity(stubPeriod, stubSubscriptionEntity);

        stubSubscriptionEntity.getPeriods().add(stubPeriodEntity);

        stubSubscriptionEntity.updateStatus(SubscriptionStatus.CANCELED.toString());

        subscriptionRepository.saveSubscription(stubSubscriptionEntity);

        jobOperatorTestUtils.setJob(downgradePlanJob);

        JobExecution execution = jobOperatorTestUtils.startJob(
                new JobParametersBuilder()
                        .addLocalDate("runDate", LocalDate.now())
                        .toJobParameters()
        );

        StepExecution stepExecution = execution.getStepExecutions().iterator().next();

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(stepExecution.getReadCount()).isEqualTo(1);
        assertThat(stepExecution.getWriteCount()).isEqualTo(1);
    }
}