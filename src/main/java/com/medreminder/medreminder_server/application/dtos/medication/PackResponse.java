package com.medreminder.medreminder_server.application.dtos.medication;

public record PackResponse(String totalAmountInPack, String currentAmountInPack, int reminderDays) {
}
