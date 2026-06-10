package com.medreminder.medreminder_server.medication;


import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventServiceImpl;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.MedicationProfileService;
import com.medreminder.medreminder_server.domain.services.medications.MedicationProfileServiceImpl;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationProfileServiceUnitTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private ProfileRepository profileRepository;

    private MedicationProfileService medicationProfileService;

    private ScheduleEventService scheduleEventService;

    private final MedicationMapper medicationMapper = new MedicationMapper();

    private final UserMapper userMapper = new UserMapper();

    @BeforeEach
    void setUp(){
        scheduleEventService =
                new ScheduleEventServiceImpl(medicationRepository, medicationMapper);

        medicationProfileService = new MedicationProfileServiceImpl(
                medicationRepository,
                profileRepository,
                medicationMapper,
                scheduleEventService,
                userMapper);
    }

    @Test
    void shouldCreateMedication_Profile_thenSaveIt() {

        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();

        final String rrule = "FREQ=DAILY;BYHOUR=8;BYMINUTE=0;BYSECOND=0";
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                rrule,"10.06.2026");

        when(profileRepository.findProfileById(any(String.class)))
                .thenReturn(Optional.of(snubProfileEntity));

        when(profileRepository.saveProfile(any(ProfileEntity.class)))
                .thenReturn(snubProfileEntity);

        MedicationProfileResponse response = medicationProfileService.createMedicationProfile(cmd);

        verify(profileRepository).saveProfile(any(ProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getMedicationName()).isEqualTo(cmd.getMedicationName());
        assertThat(response.getMedicationUnit()).isEqualTo(cmd.getMedicationUnit());
        assertThat(response.getSchedule().dosage()).isEqualTo("1.2");
        assertThat(response.getSchedule().recurrenceRule()).isEqualTo(rrule);
    }

    @Test
    void shouldCreateMedication_Profile_WithPack(){

        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();

        CreateMedicationPack pack = new CreateMedicationPack("30",7);

        final String rrule = "FREQ=DAILY;BYHOUR=8;BYMINUTE=0;BYSECOND=0";

        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                rrule,"15.06.2026", pack);

        when(profileRepository.findProfileById(any(String.class)))
                .thenReturn(Optional.of(snubProfileEntity));

        when(profileRepository.saveProfile(any(ProfileEntity.class)))
                .thenReturn(snubProfileEntity);

        MedicationProfileResponse response = medicationProfileService.createMedicationProfile(cmd);

        verify(profileRepository).saveProfile(any(ProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getPack().totalAmountInPack()).isEqualTo(pack.totalQuantity());
    }

    @Test
    void shouldUpdateMedication_Profile_thenSaveIt(){

        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();
        final String rrule = "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0";
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                rrule,"10.06.2026");

        UUID uuid = UUID.randomUUID();
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd,
                        medicationMapper, uuid.toString());

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        UpdateMedicationCommand updateCmd = new UpdateMedicationCommand(false,
                null, null, "We have just update the medication profile");

        MedicationProfileResponse response = medicationProfileService
                .updateMedicationProfile(stubMedicationProfileEntity.getId(), updateCmd);

        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull().isEqualTo(stubMedicationProfileEntity.getId());
        assertThat(response.getStatus()).isEqualTo("in_active");
        assertThat(response.getNote()).isEqualTo("We have just update the medication profile");
    }

    @Test
    void shouldUpdateScheduleRule_thenSaveIt(){

        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();
        final String rrule = "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0";
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                rrule,"15.06.2026");

        UUID uuid = UUID.randomUUID();
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd,
                        medicationMapper, uuid.toString());

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        final String updateRrule = "FREQ=DAILY;BYHOUR=10,16,20;BYMINUTE=0;BYSECOND=0";
        UpdateMedicationCommand updateCmd = new UpdateMedicationCommand(null,
                updateRrule, null, null);

        MedicationProfileResponse response = medicationProfileService
                .updateMedicationProfile(stubMedicationProfileEntity.getId(), updateCmd);

        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(LocalDateTime.parse(response.getSchedule().starTime()).getHour())
                .isEqualTo(10);
        assertThat(response.getSchedule()
                .recurrenceRule())
                .isEqualTo(updateRrule);
    }

    @Test
    void shouldUpdateDosage_thenSaveIt(){
        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0","15.06.2026");

        UUID uuid = UUID.randomUUID();
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd,
                        medicationMapper, uuid.toString());

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        UpdateMedicationCommand updateCmd = new UpdateMedicationCommand(null,
                null, "5.5", null);

        MedicationProfileResponse response = medicationProfileService
                .updateMedicationProfile(stubMedicationProfileEntity.getId(), updateCmd);

        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getSchedule().dosage()).isEqualTo("5.5");
    }

    @Test
    void shouldDeleteMedicationProfile(){

        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0","15.06.2026");

        UUID uuid = UUID.randomUUID();
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd,
                        medicationMapper, uuid.toString());

        when(medicationRepository.getMedicationProfileById(any(String.class)))
                .thenReturn(stubMedicationProfileEntity);

        ProfileEntity ownerProfileEntity = stubMedicationProfileEntity.getProfile();

        assertThat(ownerProfileEntity.getMedicationProfile().size()).isEqualTo(1);

        medicationProfileService.deleteMedicationProfile(stubMedicationProfileEntity.getId());

        verify(profileRepository).saveProfile(any(ProfileEntity.class));

        assertThat(ownerProfileEntity.getMedicationProfile().size()).isEqualTo(0);
    }


    @Test
    void shouldUpdateScheduleEvent_thenSaveIt(){

        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();

        final String rrule = "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0";
        CreateMedicationCommand cmd = MedicationStubFactory.createMedicationCommand(snubProfileEntity.getId(),
                rrule,"10.06.2026");

        UUID uuid = UUID.randomUUID();
        MedicationProfileEntity stubMedicationProfileEntity =
                MedicationStubFactory.createMedicationProfileEntity(cmd,
                        medicationMapper, uuid.toString());

        ScheduleEventEntity stubScheduleEvent = stubMedicationProfileEntity
                .getMedicationSchedule().getScheduleEvents().getFirst();

        when(medicationRepository.getScheduleEventById(any()))
                .thenReturn(stubScheduleEvent);

        Map<String, String> updateEventInput = new HashMap<>();
        updateEventInput.put("action", "TAKEN");

        ScheduleEventResponse response = scheduleEventService
                .updateScheduleEvent(stubScheduleEvent.getId(), updateEventInput);

        verify(medicationRepository).saveScheduleEvent(any(ScheduleEventEntity.class));
        verify(medicationRepository).saveMedicationProfile(any(MedicationProfileEntity.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("TAKEN");
        assertThat(response.getTakenAt()).isNotNull();
        assertThat(stubMedicationProfileEntity.getMedicationSchedule()
                .getTakenQuantity().toString()).isEqualTo("1.2");
    }
}
