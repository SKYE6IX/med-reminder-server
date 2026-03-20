package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final ProfileRepository profileRepository;
    private final MedicationMapper medicationMapper;

    public MedicationServiceImpl(MedicationRepository medicationRepository,
                                 ProfileRepository profileRepository,
                                 MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.profileRepository = profileRepository;
        this.medicationMapper = medicationMapper;

    }

    @Override
    public MedicationProfileResponse createMedication(CreateMedicationCommand cmd) {

        ProfileEntity profileEntity = profileRepository.findProfileById(cmd.getProfileId())
                .orElse(null);

        if (profileEntity == null) {
            return null;
        }

        Medication medication = createMedicationInstance(cmd);
        MedicationSchedule medicationSchedule = createMedicationSchedule(cmd.getSchedule());
        MedicationPack medicationPack = createMedicationPack(cmd.getMedicationPack()).orElse(null);

        MedicationProfile medicationProfile = new MedicationProfile(null,
                true, cmd.getMedicationNote(), medication,
                medicationSchedule, medicationPack);

        var medicationProfileEntity = medicationMapper.toEntity(medicationProfile);

        medicationProfileEntity.setProfile(profileEntity);

        MedicationProfileEntity smp = medicationRepository.saveMedicationProfile(medicationProfileEntity);

        ScheduleEventHandler scheduleHandler = new ScheduleEventHandler(medicationRepository, medicationMapper);

        scheduleHandler.createScheduleEvent(smp.getMedicationSchedule());

        return getResponse(smp, profileEntity);
    }

    @Override
    public List<ScheduleEventResponse> getMedicationScheduleEvents(String userId, String eventDate) {

        LocalDateTime startOfDay = LocalDate.parse(eventDate).atStartOfDay();
        LocalDateTime endOfDay = LocalDate.parse(eventDate).atTime(LocalTime.MAX);

        List<ScheduleEventEntity> scheduleEvents =
                medicationRepository.getMedicationScheduleByUserAndDate(userId, startOfDay, endOfDay);

        return scheduleEvents.stream()
                .map(event -> {
                    ScheduleEventResponse ser = new ScheduleEventResponse(
                            event.getId(),
                            event.getStatus(),
                            event.getMedicationSchedule().getMedicationProfile().getMedication().getName(),
                            "",
                            event.getDosage(),
                            event.getMedicationSchedule().getMedicationProfile().getMedication().getMeasurementUnit().getSymbol(),
                            event.getScheduleAt().toString());

                    ProfileEntity profile = event.getMedicationSchedule().getMedicationProfile().getProfile();
                    ser.setProfile(new ProfileResponse(profile.getId(),
                            profile.getName(),
                            profile.getRelation(), profile.isSelf()));

                    return ser;
                }).toList();
    }

    private Medication createMedicationInstance(CreateMedicationCommand cmd) {

        MeasurementUnit measurementUnit = new MeasurementUnit(null,
                Measurement.valueOf(cmd.getMedicationMeasurement()));

        return new Medication(null,
                cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit()),
                measurementUnit);
    }

    private MedicationSchedule createMedicationSchedule(CreateMedSchedule schedule) {
        DateTimeFormatter dtf= DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return new MedicationSchedule(null,
                schedule.dosage(), schedule.recurrenceRule(),
                LocalDateTime.parse(schedule.startTime(),dtf),
                LocalDateTime.parse(schedule.startDate(), dtf),
                schedule.timeZone());
    }

    private Optional<MedicationPack> createMedicationPack(CreateMedPack pack) {
        if( pack == null) {
            return Optional.empty();
        }

        MedicationPack medicationPack = new MedicationPack(null,
                pack.totalQuantity(), pack.notifyRule(), LocalDateTime.now());

        return Optional.of(medicationPack);
    }

    private static @NonNull MedicationProfileResponse getResponse(MedicationProfileEntity smp,
                                                                  ProfileEntity profileEntity) {
        String status = smp.isActive() ? "active" : "in_active";
        String createdAt = smp.getCreatedAt().isPresent() ? smp.getCreatedAt().get().toString() : "";

        MedicationProfileResponse response = new MedicationProfileResponse(smp.getId(),
                smp.getMedication().getName(),
                smp.getMedication().getUnitType(),
                status, smp.getNote(),
                createdAt);

        response.setProfile(new ProfileResponse(profileEntity.getId(),
                profileEntity.getName(), profileEntity.getRelation(), profileEntity.isSelf()));

        MedicationScheduleEntity schedule = smp.getMedicationSchedule();

        response.setSchedule(new MedScheduleResponse(schedule.getId(),
                schedule.getDoseQuantity(),
                smp.getMedication().getMeasurementUnit().getSymbol(),
                schedule.getRecurrenceRule(),
                schedule.getStartTime().toString(), schedule.getStartDate().toString()));

        return response;
    }
}