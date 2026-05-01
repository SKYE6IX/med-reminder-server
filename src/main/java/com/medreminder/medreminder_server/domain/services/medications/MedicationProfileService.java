package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;
import com.medreminder.medreminder_server.domain.services.UseCase;

import java.util.List;

public interface MedicationProfileService extends UseCase {

   MedicationProfileResponse createMedicationProfile(CreateMedicationCommand cmd);

   MedicationProfileResponse updateMedicationProfile(String medicationProfileId, UpdateMedicationCommand cmd);

   MedicationProfileResponse getMedicationProfile(String medicationProfileId);

   List<MedicationProfileResponse> getMedicationProfiles(String userId);

   void deleteMedicationProfile(String medicationProfileId);
}
