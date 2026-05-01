package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.medications.MedicationProfileService;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    private final MedicationProfileService medicationProfileService;
    private final ScheduleEventService scheduleEventService;

    public MedicationController(MedicationProfileService medicationProfileService,
                                ScheduleEventService scheduleEventService) {

        this.medicationProfileService = medicationProfileService;
        this.scheduleEventService = scheduleEventService;
    }

    @PostMapping()
    public ResponseEntity<MedicationProfileResponse> createMedicationProfile(@RequestBody CreateMedicationCommand cmd) {

        MedicationProfileResponse response = medicationProfileService.createMedicationProfile(cmd);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{medicationProfileId}")
    public ResponseEntity<MedicationProfileResponse> updateMedicationProfile(@PathVariable String medicationProfileId,
                                                                      @RequestBody UpdateMedicationCommand cmd) {

        var response = medicationProfileService.updateMedicationProfile(medicationProfileId, cmd);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping(value = "/{medicationProfileId}")
    public ResponseEntity<?> deleteMedicationProfile(@PathVariable String medicationProfileId) {

        medicationProfileService.deleteMedicationProfile(medicationProfileId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/schedules/event/{eventId}")
    public ResponseEntity<ScheduleEventResponse> updateScheduleEvent(@PathVariable String eventId,
                                                                     @RequestBody Map<String, String> eventBody) {

        var response = scheduleEventService.updateScheduleEvent(eventId, eventBody);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/schedules/event")
    public ResponseEntity<List<ScheduleEventResponse>> getMedicationsScheduleEvents(@AuthenticationPrincipal UserDetails userDetails,
                                                                                    @RequestParam String eventDate) {

        UserPrincipal principal = (UserPrincipal) userDetails;

        List<ScheduleEventResponse> response = scheduleEventService
                .getScheduleEvents(principal.getId(), eventDate);

        return ResponseEntity.ok(response);
    }
}
