package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
        toEntity(medicationProfile.getMedicationPack(),mpe).ifPresent(mpe::setMedicationPack);

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

    public Optional<MedicationPackEntity> toEntity(MedicationPack medicationPack,
                                                   MedicationProfileEntity mpe) {

        if(medicationPack == null) return Optional.empty();

        MedicationPackEntity medicationPackEntity =  new MedicationPackEntity(
                medicationPack.getId(),
                medicationPack.getTotalQuantity(),
                medicationPack.getCurrentQuantity(),
                medicationPack.getNotifyRule(),
                medicationPack.getAddedAt(),
                mpe);

        return Optional.of(medicationPackEntity);
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

       dmp.addMedicationPack(toDomain(mpe.getMedicationPack()));

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
                mse.getTimeZone());

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
                medicationPack.getNotifyRule(),
                medicationPack.getAddedAt());
    }
}