package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MedicationMapper {

    public MedicationProfileEntity toEntity(MedicationProfile medicationProfile) {

        if(medicationProfile == null) return null;

        MedicationProfileEntity medicationProfileEntity = new MedicationProfileEntity(medicationProfile.getId(),
                medicationProfile.isActive(), medicationProfile.getNote());

        medicationProfileEntity.addMedication(toEntity(medicationProfile.getMedication()));
        medicationProfileEntity.addMedicationSchedule(toEntity(medicationProfile.getMedicationSchedule()));
        toEntity(medicationProfile.getMedicationPack()).ifPresent(medicationProfileEntity::addMedicationPack);

        return medicationProfileEntity;
    }


    public MedicationEntity toEntity(Medication medication){

        if( medication == null ) return null;

        MeasurementUnitEntity measurementUnitEntity =
                new MeasurementUnitEntity(medication.getMeasurementUnit().getId(),
                        medication.getMeasurementUnit().getName().name(),
                        medication.getMeasurementUnit().getSymbol());

       MedicationEntity medicationEntity = new MedicationEntity(medication.getId(),
                medication.getName(),
                medication.getUnitType().name());

       medicationEntity.addMeasurementUnit(measurementUnitEntity);

        return medicationEntity;
    }

    public MedicationScheduleEntity toEntity(MedicationSchedule medicationSchedule){

        if(medicationSchedule == null) return null;

        return new MedicationScheduleEntity(medicationSchedule.getId(),
                medicationSchedule.getDoseQuantity(),
                medicationSchedule.getRecurrenceRule(),
                medicationSchedule.getStartTime(),
                medicationSchedule.getStartDate());
    }

    public Optional<MedicationPackEntity> toEntity(MedicationPack medicationPack) {

        if(medicationPack == null) return Optional.empty();

        MedicationPackEntity medicationPackEntity =  new MedicationPackEntity(medicationPack.getId(),
                medicationPack.getTotalQuantity(),
                medicationPack.getNotifyRule(),
                medicationPack.getAddedAt());

        return Optional.of(medicationPackEntity);
    }

    public MedicationProfile toDomain(MedicationProfileEntity mpe) {
        if( mpe == null) return null;

        return new MedicationProfile(mpe.getId(),
                mpe.isActive(),
                mpe.getNote(),
                toDomain(mpe.getMedication()),
                toDomain(mpe.getMedicationSchedule()),
                toDomain(mpe.getMedicationPack()));
    }

    public Medication toDomain(MedicationEntity medicationEntity){

        if(medicationEntity == null) return null;

        MeasurementUnit measurementUnit = new MeasurementUnit(medicationEntity.getMeasurementUnit().getId(),
                Measurement.valueOf(medicationEntity.getMeasurementUnit().getName()));

        return new Medication(medicationEntity.getId(),
                medicationEntity.getName(),
                Unit.valueOf(medicationEntity.getUnitType()), measurementUnit);
    }

    public MedicationSchedule toDomain(MedicationScheduleEntity mse){

        if(mse == null) return null;

        return new MedicationSchedule(mse.getId(),
                mse.getDoseQuantity(),
                mse.getRecurrenceRule(),
                mse.getStartTime(),
                mse.getStartDate());
    }

    public MedicationPack toDomain(MedicationPackEntity medicationPack) {
        if(medicationPack == null) return null;

        return new MedicationPack(medicationPack.getId(),
                medicationPack.getTotalQuantity(),
                medicationPack.getCurrentQuantity(),
                medicationPack.getNotifyRule(),
                medicationPack.getAddedAt());
    }
}