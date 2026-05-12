package com.medreminder.medreminder_server.application.dtos.medication;

public record RefillMedicationPackRequest(String medicationPackId,
                                          String medicationProfileId,
                                          String totalQuantity,
                                          int reminderDays) {
}
