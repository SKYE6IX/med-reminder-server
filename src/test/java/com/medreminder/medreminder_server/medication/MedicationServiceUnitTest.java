package com.medreminder.medreminder_server.medication;


import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventServiceImpl;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.MedicationService;
import com.medreminder.medreminder_server.domain.services.medications.MedicationServiceImpl;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceUnitTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private ProfileRepository profileRepository;

    private MedicationService medicationService;

    private final MedicationMapper medicationMapper = new MedicationMapper();

    private final UserMapper userMapper = new UserMapper();

    @BeforeEach
    void setUp(){
        final ScheduleEventService scheduleEventService =
                new ScheduleEventServiceImpl(medicationRepository, medicationMapper);

        medicationService = new MedicationServiceImpl(
                medicationRepository,
                profileRepository,
                medicationMapper,
                scheduleEventService,
                userMapper);
    }

    @Test
    void shouldCreateMedication_Profile_thenSaveIt() {

        ProfileEntity snubProfileEntity = MedicationStubFactory.createProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId());

        when(profileRepository.findProfileById(any(String.class)))
                .thenReturn(Optional.of(snubProfileEntity));

        when(profileRepository.saveProfile(any(ProfileEntity.class)))
                .thenReturn(snubProfileEntity);

        MedicationProfileResponse response = medicationService.createMedicationProfile(cmd);

        verify(profileRepository).saveProfile(any(ProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getMedicationName()).isEqualTo(cmd.getMedicationName());
        assertThat(response.getMedicationUnit()).isEqualTo(cmd.getMedicationUnit());
        assertThat(response.getSchedule().dosage()).isEqualTo(1.2);
        assertThat(response.getSchedule().starTime()).isEqualTo("2024-07-15T08:00");
    }

    @Test
    void shouldUpdateMedication_Profile_thenSaveIt(){

        ProfileEntity snubProfileEntity = MedicationStubFactory.createProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId());
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd, medicationMapper);

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        UpdateMedicationCommand updateCmd = new UpdateMedicationCommand(false,
                null, null, "We have just update the medication profile");

        MedicationProfileResponse response = medicationService
                .updateMedicationProfile(stubMedicationProfileEntity.getId(), updateCmd);

        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull().isEqualTo(stubMedicationProfileEntity.getId());
        assertThat(response.getStatus()).isEqualTo("in_active");
        assertThat(response.getNote()).isEqualTo("We have just update the medication profile");
    }


    @Test
    void shouldUpdateScheduleRule_thenSaveIt(){

        ProfileEntity snubProfileEntity = MedicationStubFactory.createProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId());
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd, medicationMapper);

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        UpdateMedicationCommand updateCmd = new UpdateMedicationCommand(null,
                "FREQ=DAILY;BYHOUR=10,16,20;BYMINUTE=0;BYSECOND=0", null, null);

        MedicationProfileResponse response = medicationService
                .updateMedicationProfile(stubMedicationProfileEntity.getId(), updateCmd);

        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(LocalDateTime.parse(response.getSchedule().starTime()).getHour())
                .isEqualTo(10);
    }

    @Test
    void shouldUpdateDosage_thenSaveIt(){

        ProfileEntity snubProfileEntity = MedicationStubFactory.createProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId());
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd, medicationMapper);

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        UpdateMedicationCommand updateCmd = new UpdateMedicationCommand(null,
                null, 5.5, null);

        MedicationProfileResponse response = medicationService
                .updateMedicationProfile(stubMedicationProfileEntity.getId(), updateCmd);

        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getSchedule().dosage()).isEqualTo(5.5);
    }

    @Test
    void shouldDeleteMedicationProfile_thenSaveIt(){

        ProfileEntity snubProfileEntity = MedicationStubFactory.createProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId());
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd, medicationMapper);

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        ProfileEntity ownerProfileEntity = stubMedicationProfileEntity.getProfile();

        assertThat(ownerProfileEntity.getMedicationProfile().size()).isEqualTo(1);

        medicationService.deleteMedicationProfile(stubMedicationProfileEntity.getId());

        verify(profileRepository).saveProfile(any(ProfileEntity.class));

        assertThat(ownerProfileEntity.getMedicationProfile().size()).isEqualTo(0);
    }
}
