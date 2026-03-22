package com.medreminder.medreminder_server.application.dtos.medication;

import java.util.Optional;

public class UpdateMedicationCommand {

    private Boolean isActive;
    private String startTime;
    private String recurrenceRule;
    private Double doseQuantity;
    private String note;


    public UpdateMedicationCommand() {
    }

    public UpdateMedicationCommand(Boolean isActive,
                                   String startTime,
                                   String recurrenceRule,
                                   Double doseQuantity,
                                   String note) {
        this.isActive = isActive;
        this.startTime = startTime;
        this.recurrenceRule = recurrenceRule;
        this.doseQuantity = doseQuantity;
        this.note = note;
    }


    public Optional<Boolean> getStatus() {
        if (isActive == null)
            return Optional.empty();
        return Optional.of(isActive);
    }

    public Optional<String> getStartTime() {
        if (startTime == null)
            return Optional.empty();
        return Optional.of(startTime);
    }

    public Optional<String> getRecurrenceRule() {
        if (recurrenceRule == null)
            return Optional.empty();
        return Optional.of(recurrenceRule);
    }

    public Optional<Double> getDoseQuantity() {

        if (doseQuantity == null)
            return Optional.empty();

        return Optional.of(doseQuantity);
    }

    public Optional<String> getNote() {
        if (note == null)
            return Optional.empty();
        return Optional.of(note);
    }
}
