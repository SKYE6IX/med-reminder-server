package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.application.dtos.user.UserResponse;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.services.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {

        var principal = getPrincipal(userDetails);

        var response = userService.getUser(principal.getId());

        return  ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody UpdateUserCommand cmd) {

        var principal = getPrincipal(userDetails);

        UserResponse response = userService.updateUser(principal.getId(), cmd);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {

        var principal = getPrincipal(userDetails);

        userService.deleteUser(principal.getId());

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/profiles")
    public ResponseEntity<ProfileResponse> createProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody ProfileRequest profileRequest) {

        var principal = getPrincipal(userDetails);

        ProfileResponse response = userService.createProfile(principal.getId(), profileRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/profiles")
    public ResponseEntity<List<ProfileResponse>> getProfiles(@AuthenticationPrincipal UserDetails userDetails) {
        var principal = getPrincipal(userDetails);

        List<ProfileResponse> response = userService.getProfiles(principal.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/profiles/{profileId}")
    public ResponseEntity<?> deleteProfile(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable String profileId) {

        var principal = getPrincipal(userDetails);

      userService.deleteProfile(principal.getId(), profileId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/profiles/images")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam("profileId") String profileId) {

        var principal = getPrincipal(userDetails);

        Map<String, String> response = userService.uploadProfileImage(file,principal.getId(),profileId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/profiles/images/{profileId}")
    public ResponseEntity<?> deleteProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String profileId) {

        var principal = getPrincipal(userDetails);

        userService.deleteProfileImage(principal.getId(), profileId);

        return ResponseEntity.noContent().build();
    }

    private UserPrincipal getPrincipal(UserDetails userDetails) {
        return (UserPrincipal) userDetails;
    }
}