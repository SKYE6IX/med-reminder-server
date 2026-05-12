package com.medreminder.medreminder_server.application.dtos.medication;

public record AddMedicationPackRequest(String medicationProfileId, String totalQuantity, int reminderDays) {
}
