package com.medreminder.medreminder_server.application.dtos.medication;

public record MedScheduleResponse(String id, double dosage, String measurement,
                                  String recurrenceRule, String starTime,
                                  String startDate) {
}
