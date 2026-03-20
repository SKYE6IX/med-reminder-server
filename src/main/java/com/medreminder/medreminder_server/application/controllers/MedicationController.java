package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.domain.services.medications.MedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @PostMapping(value = "/{profileId}")
    public ResponseEntity<MedicationProfileResponse> createMedication(@PathVariable String profileId,
                                                                      @RequestBody CreateMedicationCommand cmd) {

        MedicationProfileResponse response = medicationService.createMedication(profileId, cmd);

        return ResponseEntity.ok(response);
    }
}
