package com.medreminder.medreminder_server.medication;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationPack;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedSchedule;
import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.user.UserStubData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class MedicationStubFactory {

    public static CreateMedicationCommand createMedicationCommand(String profileId,
                                                                  String rrule,
                                                                  String startDate) {
        return new CreateMedicationCommand.Builder()
                .profileId(profileId)
                .medicationName("Paracetamol")
                .medicationUnit("TABLET")
                .medicationMeasurement("CAPSULE")
                .medicationNote("Take on time")
                .schedule(createMedSchedule(rrule,startDate))
                .medicationPack(null)
                .build();
    }

    public static CreateMedicationCommand createMedicationCommand(String profileId,
                                                                  String rrule,
                                                                  String stratDate,
                                                                  CreateMedicationPack createMedicationPack) {
        return new CreateMedicationCommand.Builder()
                .profileId(profileId)
                .medicationName("Paracetamol")
                .medicationUnit("TABLET")
                .medicationMeasurement("CAPSULE")
                .medicationNote("Take on time")
                .schedule(createMedSchedule(rrule,stratDate))
                .medicationPack(createMedicationPack)
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

        LocalDateTime mockLastUntilWindow = LocalDateTime.now().plusDays(1);

        MedicationSchedule medicationSchedule = new MedicationSchedule(
                null,
                new BigDecimal(cmd.getSchedule().dosage()),
                cmd.getSchedule().recurrenceRule(),
                LocalDate.parse(cmd.getSchedule().startDate(), formatter),
                cmd.getSchedule().timeZone(),
                new BigDecimal("0"),
                mockLastUntilWindow
        );
        medicationSchedule.updateStartTime(LocalDateTime.now());
        createScheduleEvents().forEach(medicationSchedule::addScheduleEvent);
        return medicationSchedule;
    }

    public static MedicationProfileEntity createMedicationProfileEntity(
            CreateMedicationCommand cmd,
            MedicationMapper medicationMapper,
            String defaultId
    ) {
        ProfileEntity snubProfileEntity = UserStubData.createStubProfileEntity();

        MedicationProfileEntity mpe = new MedicationProfileEntity(
                defaultId,
                true,
                cmd.getMedicationNote(),
                snubProfileEntity
        );
        mpe.setMedication(medicationMapper.toEntity(createMedication(cmd),mpe));
        mpe.setMedicationSchedule(medicationMapper.toEntity(createMedicationSchedule(cmd), mpe));
        snubProfileEntity.getMedicationProfile().add(mpe);
        return mpe;
    }

    private static CreateMedSchedule createMedSchedule(String rrule, String startDate) {
        return new CreateMedSchedule(
                "1.2",
                rrule,
                startDate,
                "Europe/Moscow"
        );
    }

    private static List<ScheduleEvent> createScheduleEvents() {
        return IntStream
                .range(1, 7)
                .mapToObj(i -> new ScheduleEvent(null,
                        new BigDecimal("1.2"),
                        LocalDateTime.now().plusDays(i)))
                .toList();
    }
}
