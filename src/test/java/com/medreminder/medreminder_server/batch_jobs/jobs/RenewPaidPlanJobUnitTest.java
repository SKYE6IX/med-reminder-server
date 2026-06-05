package com.medreminder.medreminder_server.batch_jobs.jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.RenewPaidPlanBatchConfig;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.subscription.SubscriptionServiceStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBatchTest
@SpringJUnitConfig(classes = {
        TestConfig.class,
        RenewPaidPlanBatchConfig.class
})
public class RenewPaidPlanJobUnitTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;


    @Autowired
    private Job renewPaidPlanJob;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
        when(paymentService.processRenewPayment(any(), any()))
                .thenReturn(SubscriptionServiceStubFactory.createMockSuccessfulPayment());
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
        stubSubscriptionEntity.updateTimeZone("Europe/Moscow");

        var stubPeriod = SubscriptionServiceStubFactory
                .createSubscriptionPeriod(null,
                        LocalDateTime.now().minusYears(1), LocalDateTime.now().minusDays(1));

        var stubPeriodEntity = subscriptionMapper
                .toEntity(stubPeriod, stubSubscriptionEntity);
        stubSubscriptionEntity.getPeriods().add(stubPeriodEntity);

        subscriptionRepository.saveSubscription(stubSubscriptionEntity);

        jobOperatorTestUtils.setJob(renewPaidPlanJob);

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
