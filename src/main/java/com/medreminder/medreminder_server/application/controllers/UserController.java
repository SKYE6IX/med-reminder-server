package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.application.dtos.user.ProfileRequest;
import com.medreminder.medreminder_server.application.dtos.user.UpdateUserCommand;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping()
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {

        return  ResponseEntity.ok(userDetails);
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody UpdateUserCommand cmd) {

//        TODO:
//        1. Verify Data being passed and reject if unwanted data passed.
//        2. Provide error for when thing go wrong.
        User user = userMapper.toDomain((UserEntity) userDetails);

        var response = userService.updateUser(user, cmd);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/profiles")
    public ResponseEntity<?> createProfile(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody ProfileRequest profileRequest) {

        User user = userMapper.toDomain((UserEntity) userDetails);

        var response = userService.createProfile(user, profileRequest);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/profiles/{profileId}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable String profileId) {

        User user = userMapper.toDomain((UserEntity) userDetails);

        userService.deleteProfile(user, profileId);

        return ResponseEntity.noContent().build();
    }

}
