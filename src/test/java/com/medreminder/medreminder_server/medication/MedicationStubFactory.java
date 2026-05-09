package com.medreminder.medreminder_server.medication;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedSchedule;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class MedicationStubFactory {

    public static ProfileEntity createProfileEntity() {
        return createProfileEntity(UUID.randomUUID());
    }

    public static ProfileEntity createProfileEntity(UUID profileId) {
        return new ProfileEntity(profileId.toString(), "test profile", "BROTHER", true);
    }

    public static CreateMedicationCommand createMedicationCommand(String profileId, String rrule) {
        return new CreateMedicationCommand.Builder()
                .profileId(profileId)
                .medicationName("Paracetamol")
                .medicationUnit("TABLET")
                .medicationMeasurement("CAPSULE")
                .medicationNote("Take on time")
                .schedule(createMedSchedule(rrule))
                .medicationPack(null)
                .build();
    }

    public static Medication createMedication(CreateMedicationCommand cmd) {

        Medication medication = new Medication(
                null,
                cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit())
        );

        MeasurementUnit measurementUnit = new MeasurementUnit(
                null, Measurement.valueOf(cmd.getMedicationMeasurement()));

        medication.addMeasurementUnit(measurementUnit);

        return medication;
    }

    public static MedicationSchedule createMedicationSchedule(CreateMedicationCommand cmd) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        MedicationSchedule medicationSchedule = new MedicationSchedule(
                null,
                new BigDecimal(cmd.getSchedule().dosage()),
                cmd.getSchedule().recurrenceRule(),
                LocalDate.parse(cmd.getSchedule().startDate(), formatter),
                cmd.getSchedule().timeZone(),
                new BigDecimal("0")
        );

        medicationSchedule.updateStartTime(LocalDateTime.now());

        createScheduleEvents().forEach(medicationSchedule::addScheduleEvent);

        return medicationSchedule;
    }

    public static MedicationProfileEntity createMedicationProfileEntity(
            CreateMedicationCommand cmd,
            MedicationMapper medicationMapper
    ) {

        UUID medicationId = UUID.randomUUID();

        ProfileEntity snubProfileEntity = MedicationStubFactory.createProfileEntity();

        MedicationProfileEntity mpe = new MedicationProfileEntity(
                medicationId.toString(),
                true,
                cmd.getMedicationNote(),
                snubProfileEntity
        );

        mpe.setMedication(medicationMapper.toEntity(createMedication(cmd),mpe));
        mpe.setMedicationSchedule(medicationMapper.toEntity(createMedicationSchedule(cmd), mpe));

        snubProfileEntity.getMedicationProfile().add(mpe);
        return mpe;
    }

    private static CreateMedSchedule createMedSchedule(String rrule) {
        return new CreateMedSchedule(
                "1.2",
                rrule,
                "15.06.2026",
                "Europe/Moscow"
        );
    }

    private static List<ScheduleEvent> createScheduleEvents() {
        return IntStream
                .range(1, 7)
                .mapToObj(i -> new ScheduleEvent(UUID.randomUUID().toString(),
                        new BigDecimal("1.2"),
                        LocalDateTime.now().plusDays(i)))
                .toList();
    }
}
