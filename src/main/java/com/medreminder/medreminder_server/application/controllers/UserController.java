package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.AppErrorResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping()
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {

        var principal = getPrincipal(userDetails);

        UserEntity userEntity = userRepository.findUserById(principal.getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        var response = new UserResponse(userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName(),
                userEntity.getDateOfBirth(),
                userEntity.getGender());

        return  ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody UpdateUserCommand cmd) {

//        TODO:
//        1. Verify Data being passed and reject if unwanted data passed.
//        2. Provide error for when thing go wrong.
//        User user = userMapper.toDomain((UserEntity) userDetails);
//
//        var response = userService.updateUser(, user, cmd);
//
//        return ResponseEntity.ok(response);

        return null;
    }

    @PostMapping(value = "/profiles")
    public ResponseEntity<ProfileResponse> createProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody ProfileRequest profileRequest) {

        var principal = getPrincipal(userDetails);

        var response = userService.createProfile(principal.getId(), profileRequest);

        return ResponseEntity.ok(new ProfileResponse(response.getId(),
                response.getName(),
                response.getRelation().name(), response.isSelf()));
    }

    @DeleteMapping(value = "/profiles/{profileId}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable String profileId) {

        var principal = getPrincipal(userDetails);

        Profile deletedProfile = userService.deleteProfile(principal.getId(), profileId);

        if (deletedProfile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppErrorResponse(HttpStatus.NOT_FOUND.value(),
                            "Profile not found!"));
        }

        return ResponseEntity.noContent().build();
    }

    private UserPrincipal getPrincipal(UserDetails userDetails) {
        return (UserPrincipal) userDetails;
    }
}
