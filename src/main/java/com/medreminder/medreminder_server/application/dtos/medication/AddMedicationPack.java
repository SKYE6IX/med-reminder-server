package com.medreminder.medreminder_server.application.dtos.medication;

public record AddMedicationPack(String medicationProfileId, String totalQuantity, String notifyRule) {
}
