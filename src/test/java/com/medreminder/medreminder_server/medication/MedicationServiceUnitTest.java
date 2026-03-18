package com.medreminder.medreminder_server.medication;


import com.medreminder.medreminder_server.application.dtos.medication.CreateMedSchedule;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.MedicationService;
import com.medreminder.medreminder_server.domain.services.medications.MedicationServiceImpl;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceUnitTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private ProfileRepository profileRepository;

    private MedicationService medicationService;

    MedicationMapper medicationMapper = new MedicationMapper();

    @BeforeEach
    void setUp(){
        medicationService = new MedicationServiceImpl(medicationRepository, profileRepository, medicationMapper);
    }


    @Test
    void shouldCreateMedication_thenSaveIt(){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        UUID profileId = UUID.randomUUID();
        UUID medicationId = UUID.randomUUID();

        ProfileEntity snubProfileEntity =
                new ProfileEntity(profileId.toString(),"test profile","BROTHER", true, null);

        CreateMedicationCommand cmd = new CreateMedicationCommand.Builder()
                .medicationName("Paracetamol")
                .medicationUnit("TABLET")
                .medicationMeasurement("CAPSULE")
                .medicationNote("Take on time")
                .schedule(new CreateMedSchedule(1.2,"every 3 days",
                        "2024-07-15T15:00:00","2024-07-15"))
                .medicationPack(null)
                .build();

        MeasurementUnit measurementUnit = new MeasurementUnit(null,
                Measurement.valueOf(cmd.getMedicationMeasurement()));
        Medication medication = new Medication(null, cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit()),measurementUnit);


        MedicationSchedule schedule = new MedicationSchedule(null, cmd.getSchedule().dosage(),
                cmd.getSchedule().recurrenceRule(),
                LocalDateTime.parse(cmd.getSchedule().startTime(), formatter),
                LocalDate.parse(cmd.getSchedule().startDate()));

        MedicationProfileEntity snubMedicationProfileEntity =
                new MedicationProfileEntity(medicationId.toString(),
                true, cmd.getMedicationNote());

        snubMedicationProfileEntity.setProfile(snubProfileEntity);
        snubMedicationProfileEntity.addMedication(medicationMapper.toEntity(medication));
        snubMedicationProfileEntity.addMedicationSchedule(medicationMapper.toEntity(schedule));


        when(profileRepository.findProfileById(any(String.class)))
                .thenReturn(Optional.of(snubProfileEntity));

        when(medicationRepository.saveMedicationProfile(any(MedicationProfileEntity.class)))
                .thenReturn(snubMedicationProfileEntity);

        MedicationProfileResponse response = medicationService.createMedication(snubProfileEntity.getId(), cmd);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull().isEqualTo(snubMedicationProfileEntity.getId());
        assertThat(response.getMedicationName()).isEqualTo(cmd.getMedicationName());
        assertThat(response.getMedicationUnit()).isEqualTo(cmd.getMedicationUnit());
        assertThat(response.getSchedule().dosage()).isEqualTo(1.2);
        assertThat(response.getSchedule().starTime()).isEqualTo("2024-07-15T15:00");
    }
}
