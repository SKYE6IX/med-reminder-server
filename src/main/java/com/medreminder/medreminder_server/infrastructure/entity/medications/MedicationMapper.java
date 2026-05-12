package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MedicationMapper {

    public MedicationProfileEntity toEntity(MedicationProfile medicationProfile,
                                            ProfileEntity profileEntity) {

        if( medicationProfile == null ) return null;

        MedicationProfileEntity mpe = new MedicationProfileEntity(
                medicationProfile.getId(),
                medicationProfile.isActive(),
                medicationProfile.getNote(),
                profileEntity
        );

        mpe.setMedication(toEntity(medicationProfile.getMedication(), mpe));

        mpe.setMedicationSchedule(toEntity(medicationProfile.getMedicationSchedule(), mpe));

        if(!medicationProfile.getMedicationPacks().isEmpty()){
            medicationProfile.getMedicationPacks()
                    .stream()
                    .map(pack -> toEntity(pack, mpe))
                    .forEach(packEntity -> mpe.getMedicationPacks().add(packEntity));
        }

        return mpe;
    }

    public MedicationEntity toEntity(Medication medication,
                                     MedicationProfileEntity mpe) {

        if( medication == null ) return null;

        MedicationEntity me = new MedicationEntity(
                medication.getId(),
                medication.getName(),
                medication.getUnitType().name(),
                mpe
        );

        MeasurementUnitEntity measurementUnitEntity = new MeasurementUnitEntity(
                medication.getMeasurementUnit().getId(),
                medication.getMeasurementUnit().getName().name(),
                medication.getMeasurementUnit().getSymbol(),
                me
        );

        me.setMeasurementUnit(measurementUnitEntity);
        return me;
    }

    public MedicationScheduleEntity toEntity(MedicationSchedule medicationSchedule,
                                             MedicationProfileEntity mpe){
        if(medicationSchedule == null) return null;

        MedicationScheduleEntity mse = new MedicationScheduleEntity(
                medicationSchedule.getId(),
                medicationSchedule.getDoseQuantity(),
                medicationSchedule.getTakenQuantity(),
                medicationSchedule.getRecurrenceRule(),
                medicationSchedule.getStartTime(),
                medicationSchedule.getStartDate(),
                medicationSchedule.getTimeZone(),
                mpe
        );

        List<ScheduleEventEntity> eventEntities = medicationSchedule
                .getScheduleEvents()
                .stream()
                .map(event -> toEntity(event, mse))
                .toList();

        mse.getScheduleEvents().addAll(eventEntities);
        return mse;
    }

    public ScheduleEventEntity toEntity(ScheduleEvent scheduleEvent,
                                        MedicationScheduleEntity mse) {
        if( scheduleEvent == null ) return null;

        return new ScheduleEventEntity(
                scheduleEvent.getId(),
                scheduleEvent.getDosage(),
                scheduleEvent.getStatus(),
                scheduleEvent.getScheduleAt(),
                mse
        );
    }

    public MedicationPackEntity toEntity(MedicationPack medicationPack,
                                                   MedicationProfileEntity mpe) {
        if(medicationPack == null) return null;

        return new MedicationPackEntity(
                medicationPack.getId(),
                medicationPack.getTotalQuantity(),
                medicationPack.getCurrentQuantity(),
                medicationPack.getReminderDays(),
                medicationPack.getStartedAt(),
                medicationPack.getStatus().toString(),
                medicationPack.isRefilled(),
                mpe);
    }

    public MedicationProfile toDomain(MedicationProfileEntity mpe) {

        if( mpe == null) return null;

       MedicationProfile dmp = new MedicationProfile(
                mpe.getId(),
                mpe.isActive(),
                mpe.getNote()
           );

       dmp.addMedication(toDomain(mpe.getMedication()));

       dmp.addMedicationSchedule(toDomain(mpe.getMedicationSchedule()));

       if(!mpe.getMedicationPacks().isEmpty()){
           mpe.getMedicationPacks()
                   .forEach(pack -> dmp.addMedicationPack(toDomain(pack)));
       }

       return dmp;
    }

    public Medication toDomain(MedicationEntity medicationEntity){

        if(medicationEntity == null) return null;

        Medication domainMedication =  new Medication(
                medicationEntity.getId(),
                medicationEntity.getName(),
                Unit.valueOf(medicationEntity.getUnitType()));

        MeasurementUnit measurementUnit = new MeasurementUnit(medicationEntity.getMeasurementUnit().getId(),
                Measurement.valueOf(medicationEntity.getMeasurementUnit().getName()));

        domainMedication.addMeasurementUnit(measurementUnit);

        return domainMedication;
    }

    public MedicationSchedule toDomain(MedicationScheduleEntity mse){

        if(mse == null) return null;

        MedicationSchedule medicationSchedule = new MedicationSchedule(
                mse.getId(),
                mse.getDoseQuantity(),
                mse.getRecurrenceRule(),
                mse.getStartDate(),
                mse.getTimeZone(),
                mse.getTakenQuantity());

        medicationSchedule.updateStartTime(mse.getStartTime());

        mse.getScheduleEvents().forEach(event -> {
            medicationSchedule.addScheduleEvent(toDomain(event));
        });

        return medicationSchedule;
    }

    public ScheduleEvent toDomain(ScheduleEventEntity see) {

        if(see == null) return null;
        return new ScheduleEvent(
                see.getId(),
                see.getDosage(),
                see.getStatus(),
                see.getScheduleAt(),
                see.getTakenAt()
        );
    }

    public MedicationPack toDomain(MedicationPackEntity medicationPack) {

        if(medicationPack == null) return null;

        return new MedicationPack(
                medicationPack.getId(),
                medicationPack.getTotalQuantity(),
                medicationPack.getCurrentQuantity(),
                medicationPack.getReminderDays(),
                medicationPack.getStartedAt(),
                medicationPack.getEndedAt(),
                MedicationPackStatus.valueOf(medicationPack.getStatus()),
                medicationPack.isRefilled()
        );
    }
}