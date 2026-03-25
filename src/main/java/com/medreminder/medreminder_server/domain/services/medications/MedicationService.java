package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;
import com.medreminder.medreminder_server.domain.services.UseCase;

import java.util.List;

public interface MedicationService extends UseCase {

   MedicationProfileResponse createMedication(CreateMedicationCommand cmd);

   MedicationProfileResponse updateMedication(String medicationProfileId, UpdateMedicationCommand cmd);

   List<ScheduleEventResponse> getMedicationScheduleEvents(String userId, String eventDate);

}
