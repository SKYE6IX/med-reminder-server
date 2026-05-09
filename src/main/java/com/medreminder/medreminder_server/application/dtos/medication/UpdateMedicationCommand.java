package com.medreminder.medreminder_server.application.dtos.medication;

import java.util.Optional;

public class UpdateMedicationCommand {

    private Boolean isActive;
    private String recurrenceRule;
    private String doseQuantity;
    private String note;

    public UpdateMedicationCommand() {
    }

    public UpdateMedicationCommand(Boolean isActive,
                                   String recurrenceRule,
                                   String doseQuantity,
                                   String note) {
        this.isActive = isActive;
        this.recurrenceRule = recurrenceRule;
        this.doseQuantity = doseQuantity;
        this.note = note;
    }

    public Optional<Boolean> getStatus() {
        if (isActive == null)
            return Optional.empty();
        return Optional.of(isActive);
    }

    public Optional<String> getRecurrenceRule() {
        if (recurrenceRule == null)
            return Optional.empty();

        return Optional.of(recurrenceRule);
    }

    public Optional<String> getDoseQuantity() {

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
