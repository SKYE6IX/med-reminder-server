package com.medreminder.medreminder_server.batch_jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.DowngradePlanBatchConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.MarkMissedDosageBatchConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.MedicationScheduleEventBatchConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.RenewPaidPlanBatchConfig;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.medication.Medication;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionStatus;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaScheduleEventRepo;
import com.medreminder.medreminder_server.infrastructure.repository.subscription.JpaSubscriptionPeriodRepo;
import com.medreminder.medreminder_server.medication.MedicationStubFactory;
import com.medreminder.medreminder_server.subscription.SubscriptionServiceStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {
        TestConfig.class,
        MedicationScheduleEventBatchConfig.class,
        DowngradePlanBatchConfig.class,
        RenewPaidPlanBatchConfig.class,
        MarkMissedDosageBatchConfig.class,
})
@ActiveProfiles("test")
public class QuartzJobsExecutionTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    @Qualifier("medScheduleEventSchedulerFactoryBean")
    private Scheduler medScheduleEventScheduler;

    @Autowired
    @Qualifier("downgradePlanSchedulerFactoryBean")
    private Scheduler downgradePlanScheduler;

    @Autowired
    @Qualifier("renewPaidPlanSchedulerFactoryBean")
    private Scheduler renewPaidPlanScheduler;

    @Autowired
    @Qualifier("markMissedDosageSchedulerFactoryBean")
    private Scheduler markMissedDosageScheduler;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    JpaScheduleEventRepo scheduleEventRepo;

    @Autowired
    private MedicationMapper medicationMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JpaSubscriptionPeriodRepo jpaSubscriptionPeriodRepo;

    @BeforeEach
    void cleanUp() {
        scheduleEventRepo.deleteAll();
    }

    @Test
    void medicationJobShouldExecuteWhenTriggeredManually() throws Exception {
        Profile snubProfile = UserStubData.createProfileWithId("John",
                Relation.BROTHER.toString(), false);

        CreateMedicationCommand cmd = MedicationStubFactory
                .createMedicationCommand(snubProfile.getId(),
                        "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0", "15.06.2026");

        Medication stubMed = MedicationStubFactory.createMedication(cmd);
        MedicationSchedule stubMedicationSchedule = MedicationStubFactory.createMedicationSchedule(cmd);
        MedicationProfileEntity stubMedicationProfile = new MedicationProfileEntity(
                null,
                true,
                cmd.getMedicationNote(),
                null,
                null
        );
        stubMedicationProfile.setMedication(medicationMapper.toEntity(stubMed,stubMedicationProfile));
        stubMedicationProfile.setMedicationSchedule(medicationMapper.toEntity(stubMedicationSchedule, stubMedicationProfile));

        medicationRepository.saveMedicationProfile(stubMedicationProfile);

        long totalEventsBeforeJob = scheduleEventRepo.count();

        medScheduleEventScheduler.triggerJob(JobKey.jobKey("medication_schedule_event_job_detail"));

        await().atMost(10, SECONDS)
                .untilAsserted(() ->{
                    long count = scheduleEventRepo.count();
                    assertThat(count).isGreaterThan(totalEventsBeforeJob);
                }
                );
    }

    @Test
    void downgradePlanJobShouldExecuteWhenTriggeredManually() throws Exception {
        User stubUser = UserStubData
                .createUser(null,"testdowngrade@mail.com","test user", null);
        var plan = SubscriptionServiceStubFactory
                .createPlan(null);
        plan.toProPlan();

        var stubUserEntity = userMapper.toEntity(stubUser);

        stubUserEntity.setPlan(subscriptionMapper.toEntity(plan, stubUserEntity));

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

        subscriptionRepository
                .saveSubscription(stubSubscriptionEntity);

        downgradePlanScheduler.triggerJob(JobKey.jobKey("downgrade_plan_job_detail"));

        await().atMost(10, SECONDS)
                .untilAsserted(() -> {
                    subscriptionRepository.getPlanByUserId(stubUserEntity.getId())
                            .ifPresent(planEntity -> {
                                assertThat(planEntity.getPlanType()).isEqualTo(PlanType.FREE.toString());
                            });
                });
    }


    @Test
    void renewPaidPlanJobShouldExecuteWhenTriggeredManually() throws Exception {
        User stubUser = UserStubData
                .createUser(null,"testrenew@mail.com","test user", null);
        var plan = SubscriptionServiceStubFactory
                .createPlan(null);
        plan.toProPlan();

        var stubUserEntity = userMapper.toEntity(stubUser);
        stubUserEntity.setPlan(subscriptionMapper.toEntity(plan, stubUserEntity));

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

        when(paymentService.processRenewPayment(any(), any()))
                .thenReturn(SubscriptionServiceStubFactory.createMockSuccessfulPayment());

        renewPaidPlanScheduler.triggerJob(JobKey.jobKey("renew_paid_plan_job_detail"));

        await().atMost(15, SECONDS)
                .untilAsserted(() -> {
                    long count = jpaSubscriptionPeriodRepo.count();
                    assertThat(count).isGreaterThan(1);
                });
    };

    @Test
    void markMissedDosageJobShouldExecuteWhenTriggeredManually() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDateTime yesterday = now
                .minusDays(1).atTime(6,30,0);
        ScheduleEventEntity scheduleEventEntity = new ScheduleEventEntity(
                null,
                new BigDecimal("2.1"),
                "PENDING",
                yesterday,
                null
        );

        var savedData = scheduleEventRepo.save(scheduleEventEntity);

        markMissedDosageScheduler.triggerJob(JobKey.jobKey("mark_missed_dosage_job_detail"));

        await().atMost(15, SECONDS)
                .untilAsserted(() -> {
                    scheduleEventRepo.findById(savedData.getId())
                            .ifPresent(event-> {
                                assertThat(event.getStatus()).isEqualTo("MISSED");
                            });
                });
    };
}
