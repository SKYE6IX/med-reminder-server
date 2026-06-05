package com.medreminder.medreminder_server.batch_jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.DowngradePlanBatchConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.MedicationScheduleEventBatchConfig;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.Medication;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
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
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaScheduleEventRepo;
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

import java.time.LocalDateTime;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringJUnitConfig(classes = {
        TestConfig.class,
        MedicationScheduleEventBatchConfig.class,
        DowngradePlanBatchConfig.class
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
    private MedicationRepository medicationRepository;

    @Autowired
    JpaScheduleEventRepo scheduleEventRepo;

    @Autowired
    private MedicationMapper medicationMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

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
                null
        );
        stubMedicationProfile.setMedication(medicationMapper.toEntity(stubMed,stubMedicationProfile));
        stubMedicationProfile.setMedicationSchedule(medicationMapper.toEntity(stubMedicationSchedule, stubMedicationProfile));

        medicationRepository.saveMedicationProfile(stubMedicationProfile);

        medScheduleEventScheduler.triggerJob(JobKey.jobKey("medication_schedule_event_job_detail"));

        await().atMost(10, SECONDS)
                .untilAsserted(() ->{
                    long count = scheduleEventRepo.count();
                    assertThat(count).isEqualTo(20);
                }
                );
    }

    @Test
    void downgradePlanJobShouldExecuteWhenTriggeredManually() throws Exception {
        User stubUser = UserStubData
                .createUser(null,"test@mai.com","test user", null);
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
}
