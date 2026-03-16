package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.Medication;
import com.medreminder.medreminder_server.domain.models.medication.MedicationPack;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import org.springframework.stereotype.Component;

@Component
public class MedicationMapper {

    public MedicationEntity toEntity(Medication medication){

        if(medication == null) return null;

        MeasurementUnitEntity measurementUnitEntity =
                new MeasurementUnitEntity(medication.getMeasurementUnit().getId(),
                        medication.getMeasurementUnit().getName().name(),
                        medication.getMeasurementUnit().getSymbol());

        return new MedicationEntity(medication.getId(),
                medication.getName(),
                medication.getUnitType().name(),
                measurementUnitEntity);
    }

    public MedicationScheduleEntity toEntity(MedicationSchedule medicationSchedule){

        if(medicationSchedule == null) return null;

        return new MedicationScheduleEntity(medicationSchedule.getId(),
                medicationSchedule.getDoseQuantity(),
                medicationSchedule.getRecurrenceRule(),
                medicationSchedule.getStartAt());
    }

    public MedicationPackEntity toEntity(MedicationPack medicationPack) {

        if(medicationPack == null) return null;

        return new MedicationPackEntity(medicationPack.getId(),
                medicationPack.getTotalQuantity(),
                medicationPack.getNotifyRule(),
                medicationPack.getAddedAt());
    }
}
