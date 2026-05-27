package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private UserPrincipal getPrincipal(UserDetails userDetails) {
        return (UserPrincipal) userDetails;
    }
}