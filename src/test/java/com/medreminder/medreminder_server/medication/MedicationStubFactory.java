package com.medreminder.medreminder_server.medication;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedSchedule;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class MedicationStubFactory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ProfileEntity createProfileEntity() {
        return createProfileEntity(UUID.randomUUID());
    }

    public static ProfileEntity createProfileEntity(UUID profileId) {
        return new ProfileEntity(profileId.toString(), "test profile", "BROTHER", true, null);
    }

    public static CreateMedicationCommand createMedicationCommand(String profileId) {
        return new CreateMedicationCommand.Builder()
                .profileId(profileId)
                .medicationName("Paracetamol")
                .medicationUnit("TABLET")
                .medicationMeasurement("CAPSULE")
                .medicationNote("Take on time")
                .schedule(createMedSchedule())
                .medicationPack(null)
                .build();
    }

    public static CreateMedSchedule createMedSchedule() {
        return new CreateMedSchedule(
                1.2,
                "FREQ=DAILY;BYHOUR=8,20;BYMINUTE=0;BYSECOND=0",
                "2024-07-15T15:00:00",
                "Europe/Moscow"
        );
    }

    public static List<ScheduleEvent> createScheduleEvent() {
        return IntStream.range(1, 7)
                .mapToObj(i -> new ScheduleEvent(null,5,
                        LocalDateTime.now().plusDays(i)))
                .toList();
    }

    public static Medication createMedication(CreateMedicationCommand cmd) {
        MeasurementUnit measurementUnit = new MeasurementUnit(
                null, Measurement.valueOf(cmd.getMedicationMeasurement()));

        return new Medication(
                null,
                cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit()),
                measurementUnit
        );
    }

    public static MedicationSchedule createMedicationSchedule(CreateMedicationCommand cmd) {
        return new MedicationSchedule(
                null,
                cmd.getSchedule().dosage(),
                cmd.getSchedule().recurrenceRule(),
                LocalDateTime.parse(cmd.getSchedule().startDate(), FORMATTER),
                cmd.getSchedule().timeZone()
        );
    }

    public static MedicationProfileEntity createMedicationProfileEntity(
            ProfileEntity profileEntity,
            CreateMedicationCommand cmd,
            MedicationMapper medicationMapper
    ) {
        UUID medicationId = UUID.randomUUID();

        MedicationProfileEntity entity =
                new MedicationProfileEntity(medicationId.toString(), true, cmd.getMedicationNote());

        entity.setProfile(profileEntity);
        entity.addMedication(medicationMapper.toEntity(createMedication(cmd)));
        entity.addMedicationSchedule(medicationMapper.toEntity(createMedicationSchedule(cmd)));

        entity.getMedicationSchedule()
                .addStartTime(LocalDateTime.parse("2024-07-15T08:00"));
        return entity;
    }
}
