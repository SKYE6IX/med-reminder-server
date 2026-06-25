package com.medreminder.medreminder_server.application.dtos.medication;

public record MedScheduleResponse(String id,
                                  String dosage,
                                  String measurement,
                                  String recurrenceRule,
                                  String starTime,
                                  String startDate,
                                  String endDate,
                                  String amountTaken) {
}