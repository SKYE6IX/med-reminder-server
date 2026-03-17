package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;

public interface MedicationService {

   MedicationProfileResponse createMedication(String profileId, CreateMedicationCommand cmd);
}
