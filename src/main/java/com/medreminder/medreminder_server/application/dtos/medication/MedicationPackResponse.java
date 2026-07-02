package com.medreminder.medreminder_server.application.dtos.medication;

public record MedicationPackResponse(String id,
                                     String status,
                                     String startedAt,
                                     String endedAt,
                                     String totalQuantity,
                                     String currentQuantity,
                                     boolean isRefilled,
                                     int reminderDays,
                                     String medicationProfileId,
                                     String medicationName,
                                     String medicationImageUrl,
                                     String dosageAmount,
                                     String dosageMeasurement) {
}