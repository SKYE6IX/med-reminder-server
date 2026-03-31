package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.medication.CreateMedicationCommand;
import com.medreminder.medreminder_server.application.dtos.medication.MedicationProfileResponse;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.medication.UpdateMedicationCommand;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.medications.MedicationService;
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

    private final MedicationService medicationService;
    private final ScheduleEventService scheduleEventService;

    public MedicationController(MedicationService medicationService,
                                ScheduleEventService scheduleEventService) {

        this.medicationService = medicationService;
        this.scheduleEventService = scheduleEventService;
    }

    @PostMapping()
    public ResponseEntity<MedicationProfileResponse> createMedication(@RequestBody CreateMedicationCommand cmd) {

        MedicationProfileResponse response = medicationService.createMedicationProfile(cmd);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{medicationProfileId}")
    public ResponseEntity<MedicationProfileResponse> updateMedication(@PathVariable String medicationProfileId,
                                                                      @RequestBody UpdateMedicationCommand cmd) {

        var response = medicationService.updateMedicationProfile(medicationProfileId, cmd);

        return ResponseEntity.ok(response);

    }

    @PutMapping(value = "/schedules/event/{eventId}")
    public ResponseEntity<ScheduleEventResponse> updateScheduleEvent(@PathVariable String eventId,
                                                                     @RequestBody Map<String, String> eventBody) {

        var response = scheduleEventService.updateScheduleEvents(eventId, eventBody);

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
