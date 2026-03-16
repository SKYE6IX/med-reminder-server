package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;

public interface MedicationService {

    void createMedication(String profileId, CreateMedicationCommand cmd);
}
