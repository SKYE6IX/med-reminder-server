package com.medreminder.medreminder_server.application.controllers;


import com.medreminder.medreminder_server.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    public UserController(UserService userService) {
    }

    @GetMapping()
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {

        return  ResponseEntity.ok(userDetails);
    }

}
