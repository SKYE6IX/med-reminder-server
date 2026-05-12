package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.medication.*;
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
    public ResponseEntity<MedicationProfileResponse> createMedicationProfile(
            @RequestBody CreateMedicationCommand cmd) {

        MedicationProfileResponse response = medicationProfileService
                .createMedicationProfile(cmd);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{medicationProfileId}")
    public ResponseEntity<MedicationProfileResponse> getAllMedicationProfile(
            @PathVariable String medicationProfileId) {

        MedicationProfileResponse response = medicationProfileService
                .getMedicationProfile(medicationProfileId);

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<MedicationProfileResponse>> getAllMedicationProfiles(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserPrincipal principal = (UserPrincipal) userDetails;

        List<MedicationProfileResponse> responses = medicationProfileService
                .getMedicationProfiles(principal.getId());

        return ResponseEntity.ok(responses);
    }

    @PutMapping(value = "/{medicationProfileId}")
    public ResponseEntity<MedicationProfileResponse> updateMedicationProfile(
            @PathVariable String medicationProfileId,
            @RequestBody UpdateMedicationCommand cmd) {

        var response = medicationProfileService
                .updateMedicationProfile(medicationProfileId, cmd);

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
    public ResponseEntity<List<ScheduleEventResponse>> getMedicationsScheduleEvents(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String eventDate) {

        UserPrincipal principal = (UserPrincipal) userDetails;

        List<ScheduleEventResponse> response = scheduleEventService
                .getScheduleEvents(principal.getId(), eventDate);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/packs")
    public ResponseEntity<Map<String, String>> createMedicationPack(
            @RequestBody AddMedicationPackRequest addMedicationPackRequest){

        Map<String, String> response = medicationProfileService
                .createMedicationPack(addMedicationPackRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/packs/refill")
    public ResponseEntity<RefillMedicationPackResponse> refillMedicationPack(
            @RequestBody RefillMedicationPackRequest refillMedicationPackRequest) {

        RefillMedicationPackResponse response = medicationProfileService
                .refillMedicationPack(refillMedicationPackRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/packs/refill")
    public ResponseEntity<List<RefillMedicationPackResponse>> getRefillMedicationPacks(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserPrincipal principal = (UserPrincipal) userDetails;

        List<RefillMedicationPackResponse> response = medicationProfileService
                .getRefillMedicationPacks(principal.getId());

        return ResponseEntity.ok(response);
    }
}