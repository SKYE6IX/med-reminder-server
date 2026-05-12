package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.domain.services.UseCase;

import java.util.List;
import java.util.Map;

public interface MedicationProfileService extends UseCase {

   MedicationProfileResponse createMedicationProfile(CreateMedicationCommand cmd);

   MedicationProfileResponse updateMedicationProfile(String medicationProfileId, UpdateMedicationCommand cmd);

   MedicationProfileResponse getMedicationProfile(String medicationProfileId);

   List<MedicationProfileResponse> getMedicationProfiles(String userId);

   void deleteMedicationProfile(String medicationProfileId);

   Map<String, String> createMedicationPack(AddMedicationPackRequest addMedicationPackRequest);

   RefillMedicationPackResponse  refillMedicationPack(RefillMedicationPackRequest refillMedicationPackRequest);

   List<RefillMedicationPackResponse> getRefillMedicationPacks(String userId);
}