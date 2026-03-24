package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.medications.MedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @PostMapping()
    public ResponseEntity<MedicationProfileResponse> createMedication(@RequestBody CreateMedicationCommand cmd) {

        MedicationProfileResponse response = medicationService.createMedication(cmd);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{medicationProfileId}")
    public ResponseEntity<MedicationProfileResponse> updateMedication(@PathVariable String medicationProfileId,
                                                                      @RequestBody UpdateMedicationCommand cmd) {

        var response = medicationService.updateMedication(medicationProfileId, cmd);

        return ResponseEntity.ok(response);

    }

    @GetMapping(value = "/schedules/event")
    public ResponseEntity<List<ScheduleEventResponse>> getMedicationsSchedules(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestParam String eventDate) {

        UserPrincipal principal = (UserPrincipal) userDetails;

        var response = medicationService.getMedicationScheduleEvents(principal.getId(), eventDate);

        return ResponseEntity.ok(response);
    }
}
