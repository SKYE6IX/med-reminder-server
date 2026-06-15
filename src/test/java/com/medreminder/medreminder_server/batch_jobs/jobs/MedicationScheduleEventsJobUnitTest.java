package com.medreminder.medreminder_server.batch_jobs.jobs;


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
import com.medreminder.medreminder_server.medication.MedicationStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBatchTest
@SpringJUnitConfig(classes = {
        TestConfig.class,
        MedicationScheduleEventBatchConfig.class
})
@ActiveProfiles("test")
public class MedicationScheduleEventsJobUnitTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    @Qualifier("medicationScheduleEventJob")
    private Job medicationScheduleEventJob;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private MedicationMapper medicationMapper;

    @BeforeEach
    void setup() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void jobShouldProcessOnlyEligibleAndCompleteSuccessfully() throws Exception {
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

        jobOperatorTestUtils.setJob(medicationScheduleEventJob);

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