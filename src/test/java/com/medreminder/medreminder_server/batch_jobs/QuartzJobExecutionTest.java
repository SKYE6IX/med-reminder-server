package com.medreminder.medreminder_server.batch_jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.MedicationScheduleEventBatchConfig;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.Medication;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.models.users.Relation;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.repository.medications.JpaScheduleEventRepo;
import com.medreminder.medreminder_server.medication.MedicationStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringJUnitConfig(classes = {
        TestConfig.class,
        MedicationScheduleEventBatchConfig.class
})
@ActiveProfiles("test")
public class QuartzJobExecutionTest {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    JpaScheduleEventRepo scheduleEventRepo;

    @Autowired
    private MedicationMapper medicationMapper;

    @BeforeEach
    void cleanUp() {
        scheduleEventRepo.deleteAll();
    }

    @Test
    void medicationJobShouldExecuteWhenTriggeredManually() throws Exception {
        Profile snubProfile = UserStubData.createStubProfileWithId("John",
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

        scheduler.triggerJob(JobKey.jobKey("medicationScheduleEventScheduler"));

        await().atMost(10, SECONDS)
                .untilAsserted(() ->
                        assertThat(scheduleEventRepo.count()).isEqualTo(20)
                );

    }
}
