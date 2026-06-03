package com.medreminder.medreminder_server.batch_jobs.processing;


import com.medreminder.medreminder_server.application.batch_jobs.processing.MedicationScheduleEventProcessor;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.medication.MedicationStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationScheduleProcessorUnitTest {
    @Mock
    private MedicationRepository medicationRepository;

    private MedicationMapper medicationMapper;

    private MedicationScheduleEventProcessor processor;

    @BeforeEach
    void setUp() {
        medicationMapper = new MedicationMapper();
        ScheduleEventService scheduleEventService =
                new ScheduleEventServiceImpl(medicationRepository, medicationMapper);
        processor = new MedicationScheduleEventProcessor(scheduleEventService, medicationMapper);
    }

    @Test
    public void shouldCreateScheduleEvents() {
        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();

        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                "FREQ=DAILY;BYHOUR=8;BYMINUTE=0;BYSECOND=0");
        MedicationProfileEntity snubMedicationProfile = MedicationStubFactory
                .createMedicationProfileEntity(cmd, medicationMapper);

        MedicationScheduleEntity result = processor.process(snubMedicationProfile.getMedicationSchedule());

        assertThat(result).isNotNull();
        assertThat(result.getScheduleEvents().size()).isGreaterThan(7);
        assertThat(result.getLastExpandedUntil()).isNotNull();
    }
}
