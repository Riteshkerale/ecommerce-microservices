package com.ritesh.user_service.controller;

import com.ritesh.user_service.dtos.Request.UpdateUserRequest;
import com.ritesh.user_service.dtos.Request.UserRegisterRequest;
import com.ritesh.user_service.dtos.Response.UserResponse;
import com.ritesh.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // Register User
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRegisterRequest request) {

        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // Get User By ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }




}