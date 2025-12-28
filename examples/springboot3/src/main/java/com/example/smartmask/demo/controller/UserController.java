package com.example.smartmask.demo.controller;

import com.example.smartmask.demo.model.User;
import com.example.smartmask.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/public/user")
    public User getPublicUser() {
        return userService.getUserById(1L);
    }

    @GetMapping("/admin/user")
    public User getAdminUser() {
        // Same user data, but when accessed with admin role,
        // fields with rolesAllowed={"ROLE_ADMIN"} will be unmasked
        return userService.getUserById(1L);
    }

    @PostMapping("/public/user")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
