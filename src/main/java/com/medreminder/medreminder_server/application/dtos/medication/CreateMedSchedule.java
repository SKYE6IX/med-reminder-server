package com.medreminder.medreminder_server.application.dtos.medication;

public record CreateMedSchedule(String dosage,
                                String recurrenceRule,
                                String startDate,
                                String endDate,
                                String timeZone) {}
