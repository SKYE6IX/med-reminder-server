package com.medreminder.medreminder_server.application.dtos.medication;

public record NewMedicationPackRequest(String medicationProfileId, String totalQuantity, int reminderDays) {
}
