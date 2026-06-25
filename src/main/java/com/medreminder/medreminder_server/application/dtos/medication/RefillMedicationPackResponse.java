package com.medreminder.medreminder_server.application.dtos.medication;

public record RefillMedicationPackResponse(String id,
                                           String status,
                                           String startedAt,
                                           String totalQuantity,
                                           String medicationName,
                                           String medicationImageUrl,
                                           String medicationProfileId,
                                           String dosageAmount,
                                           String dosageMeasurement) {
}
