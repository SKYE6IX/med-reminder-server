package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationPackEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Helper {
    public static Medication createMedication(CreateMedicationCommand cmd) {
        return new Medication(null,
                cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit()),
                Measurement.valueOf(cmd.getMedicationMeasurement()));
    }

    public static MedicationSchedule createMedicationSchedule(CreateMedSchedule schedule) {

        LocalDate startDate = LocalDate.parse(schedule.startDate(), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate endDate = schedule.endDate() != null ?
                LocalDate.parse(schedule.endDate(), DateTimeFormatter.BASIC_ISO_DATE) : null;

        return new MedicationSchedule(
                null,
                new BigDecimal(schedule.dosage()),
                schedule.recurrenceRule(),
                startDate,
                endDate,
                new BigDecimal("0"),
                null);
    }

   public static Optional<MedicationPack> createMedicationPack(CreateMedicationPack pack,
                                                               String timeZone) {
        if( pack == null) {
            return Optional.empty();
        }

        ZoneId zoneId = ZoneId.of(timeZone);

        MedicationPack medicationPack = new MedicationPack(
                null,
                new BigDecimal(pack.totalQuantity()),
                new BigDecimal(pack.totalQuantity()),
                pack.reminderDays(),
                LocalDateTime.now(zoneId),
                null,
                MedicationPackStatus.ACTIVE,
                false);
        return Optional.of(medicationPack);
    }

    public static void syncMedicationProfiles(List<MedicationProfile> domainMedicationProfiles,
                                              ProfileEntity managedProfile) {
        final MedicationMapper medicationMapper = new MedicationMapper();

        Map<String, MedicationProfileEntity> existingMedicationProfiles = new HashMap<>();

        List<MedicationProfileEntity> syncedMedicationProfiles = new ArrayList<>();

//      We only add medicationProfiles into the existingMedicationProfiles
//      Only if user has an existing one.
        if(!managedProfile.getMedicationProfile().isEmpty()){
            var toMaps = managedProfile.getMedicationProfile()
                    .stream()
                    .collect(Collectors.toMap(MedicationProfileEntity::getId,
                            emp -> emp));
            existingMedicationProfiles.putAll(toMaps);
        }

//        We check if the domain medication profile is empty, which mean user hasn't got
//        anymore medication profile. This is the case for when user delete their medication
//        profile.
        if(domainMedicationProfiles.isEmpty()) {
//            If the list is empty, we need to make sure that the managed also is.
            managedProfile.getMedicationProfile().clear();
        } else {
            var list = domainMedicationProfiles
                    .stream()
                    .map(medProfile -> existingMedicationProfiles
                            .getOrDefault(medProfile.getId(),
                                    medicationMapper.toEntity(medProfile, managedProfile)))
                    .toList();

            syncedMedicationProfiles.addAll(list);

            managedProfile.getMedicationProfile().clear();

            managedProfile.getMedicationProfile().addAll(syncedMedicationProfiles);
        }
    }

    public static MedicationProfileResponse getMedicationProfileResponse(MedicationProfileEntity smp) {
        return getMedicationProfileResponse(smp, smp.getProfile());
    }

    public static MedicationProfileResponse getMedicationProfileResponse(MedicationProfileEntity smp,
                                                                   ProfileEntity profileEntity) {
        String status = smp.isActive() ? "active" : "in_active";
//        Create a new Medication profile response object
        MedicationProfileResponse response = new MedicationProfileResponse(
                smp.getId(),
                smp.getMedication().getName(),
                smp.getMedication().getUnitType(),
                status,
                smp.getNote(),
                smp.getMedicationReason());

//        Attach the selected profile to the response
        response.setProfile(new ProfileResponse(
                profileEntity.getId(),
                profileEntity.getAvatarUrl(),
                profileEntity.getName(),
                profileEntity.getRelation(),
                profileEntity.isSelf()));

//        Acquire the schedule and create an object and attached it to the response
        MedicationScheduleEntity schedule = smp.getMedicationSchedule();

        final String endDate = schedule.getEndDate() != null ? schedule.getEndDate().toString() : null;

        response.setSchedule(new MedScheduleResponse(
                schedule.getId(),
                schedule.getDoseQuantity().stripTrailingZeros().toPlainString(),
                smp.getMedication().getMeasurement(),
                schedule.getRecurrenceRule(),
                schedule.getStartTime().toString(),
                schedule.getStartDate().toString(),
                endDate,
                schedule.getTakenQuantity().stripTrailingZeros().toPlainString())
        );

        if(!smp.getMedicationPacks().isEmpty()){
            smp.getMedicationPacks()
                    .stream()
                    .filter(packEntity -> packEntity.getStatus().equals("ACTIVE"))
                    .findFirst()
                    .ifPresent(medicationPack -> {
                        response.setPack(medicationPack.getTotalQuantity().stripTrailingZeros().toPlainString(),
                                medicationPack.getCurrentQuantity().stripTrailingZeros().toPlainString() );
                    });
        }
        return response;
    }

    public static void syncMedicationPack(MedicationProfileEntity managedMedicationProfile,
                                          MedicationPack medicationPack) {
        managedMedicationProfile.getMedicationPacks()
                .stream()
                .filter(mpe->  mpe.getId().equals(medicationPack.getId()))
                .findFirst()
                .ifPresent(mpe-> mpe.updateMedicationPack(medicationPack));
    }

    public static MedicationPack getMedicationPackByStatus(MedicationProfileEntity managedMedicationProfile,
                                                           String status,
                                                           MedicationMapper medicationMapper) {
        return managedMedicationProfile
                .getMedicationPacks()
                .stream()
                .filter(mpe -> mpe.getStatus().equals(status))
                .findFirst()
                .map(medicationMapper::toDomain)
                .orElse(null);
    }

    public static MedicationPackResponse getMedicationPackResponse(MedicationPackEntity medicationPack) {

        var medicationProfile = medicationPack.getMedicationProfile();

        String startedAt = medicationPack.getStartedAt() != null ? medicationPack.getStartedAt().toString() : null;
        String endedAt = medicationPack.getEndedAt() != null ? medicationPack.getEndedAt().toString() : null;

        return new MedicationPackResponse(
                medicationPack.getId(),
                medicationPack.getStatus(),
                startedAt,
                endedAt,
                medicationPack.getTotalQuantity().stripTrailingZeros().toPlainString(),
                medicationPack.getCurrentQuantity().stripTrailingZeros().toPlainString(),
                medicationPack.isRefilled(),
                medicationPack.getReminderDays(),
                medicationProfile.getId(),
                medicationProfile.getMedication().getName(),
                "",
                medicationProfile.getMedicationSchedule().getDoseQuantity().stripTrailingZeros().toPlainString(),
                medicationProfile.getMedication().getMeasurement()
        );
    }
}
