package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;

import java.util.List;

public interface MedicationService {

   MedicationProfileResponse createMedication(CreateMedicationCommand cmd);

   MedicationProfileResponse updateMedication(String medicationProfileId, UpdateMedicationCommand cmd);

   List<ScheduleEventResponse> getMedicationScheduleEvents(String userId, String eventDate);


//   TODO:
//   Setting up updates for Medications;
//   Not all fields need to be updated.
//   Fields that need updating>
//   >Status✅
//   Schedule starting time
//   Update frequency
//   update dosage
//   update note ✅
}
