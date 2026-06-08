package com.medreminder.medreminder_server.batch_jobs.processing;


import com.medreminder.medreminder_server.application.batch_jobs.processing.MarkMissedDosageProcessor;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MarkMissedDosageProcessorUnitTest {

    private final MedicationMapper medicationMapper = new MedicationMapper();


    private MarkMissedDosageProcessor processor;

    @BeforeEach
    public void setUp() {
        processor = new MarkMissedDosageProcessor(medicationMapper);
    }


    @Test
    void shouldMarkMissedDosage() {
        ScheduleEventEntity scheduleEventEntity = new ScheduleEventEntity(
                null,
                new BigDecimal("2.1"),
                "PENDING",
                LocalDateTime.now(),
                null
        );

        ScheduleEventEntity result = processor.process(scheduleEventEntity);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("MISSED");
    }
}
