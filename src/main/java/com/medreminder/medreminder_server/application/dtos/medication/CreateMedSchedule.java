package com.medreminder.medreminder_server.application.dtos.medication;

public record CreateMedSchedule(double dosage, String recurrenceRule, String startAt){}
