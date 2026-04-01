package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.error.AppErrorResponse;
import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.application.dtos.user.UserResponse;
import com.medreminder.medreminder_server.application.security.UserPrincipal;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.domain.services.users.UserService;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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

        var response = userService.getUserById(principal.getId());

        return  ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody UpdateUserCommand cmd) {

        var principal = getPrincipal(userDetails);

        var response = userService.updateUser(principal.getId(), cmd);

        return ResponseEntity.ok(
                new UserResponse(response.getId(),
                        response.getEmail(), response.getName(),
                        response.getDateOfBirth(), response.getGender())
        );
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {

        var principal = getPrincipal(userDetails);

        UserResponse userEntity = userService.getUserById(principal.getId());

        if( userEntity == null ){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("User not found!");
        }

        userService.deleteUser(principal.getId());

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/profiles")
    public ResponseEntity<ProfileResponse> createProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody ProfileRequest profileRequest) {

        var principal = getPrincipal(userDetails);

        var response = userService.createProfile(principal.getId(), profileRequest);

        return ResponseEntity.ok(new ProfileResponse(
                response.getId(),
                response.getName(),
                response.getRelation().name(), response.isSelf()));
    }

    @DeleteMapping(value = "/profiles/{profileId}")
    public ResponseEntity<?> deleteProfile(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable String profileId) {

        var principal = getPrincipal(userDetails);

      userService.deleteProfile(principal.getId(), profileId);

        return ResponseEntity.noContent().build();
    }

    private UserPrincipal getPrincipal(UserDetails userDetails) {
        return (UserPrincipal) userDetails;
    }
}
