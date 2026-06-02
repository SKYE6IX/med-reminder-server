package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.application.dtos.subscription.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping()
    public ResponseEntity<SubscriptionPlanResponse> getSubscriptionPlan(
            @AuthenticationPrincipal UserDetails userDetails) {

        var principal = getPrincipal(userDetails);

        SubscriptionPlanResponse response = subscriptionService.getSubscriptionPlanByUserId(principal.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<SubscriptionPlanResponse> createSubscriptionPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PaidSubscriptionRequest requestBody) {

        var principal = getPrincipal(userDetails);

       SubscriptionPlanResponse response =
               subscriptionService.createPaidSubscriptionPlan(requestBody, principal.getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<Map<String, String>> cancelSubscriptionPlan(
            @AuthenticationPrincipal UserDetails userDetails){

        var principal = getPrincipal(userDetails);

        Map<String,String> response =
                subscriptionService.cancelPaidSubscriptionPlan(principal.getId());

        return ResponseEntity.ok(response);
    }

    private UserPrincipal getPrincipal(UserDetails userDetails) {
        return (UserPrincipal) userDetails;
    }
}