package com.ritesh.user_service.controller;

import com.ritesh.user_service.dtos.Request.LoginRequest;
import com.ritesh.user_service.dtos.Request.UpdateUserRequest;
import com.ritesh.user_service.dtos.Request.UserRegisterRequest;
import com.ritesh.user_service.dtos.Response.LoginResponse;
import com.ritesh.user_service.dtos.Response.UserResponse;
import com.ritesh.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // Login User
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }


    // Get User By ID
    // USER → own profile
    // ADMIN → any user
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);

        return ResponseEntity.ok(response);
    }


    // Get All Users
    // ADMIN only
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }


    // Update User
    // USER → own profile
    // ADMIN → any user
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response =
                userService.updateUser(id, request);

        return ResponseEntity.ok(response);
    }


    // Delete User
    // USER → own account
    // ADMIN → any user
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");
    }


    // Test USER role
    @GetMapping("/test-user")
    public String testUser() {

        return "USER access successful";
    }


    // Test ADMIN role
    @GetMapping("/test-admin")
    public String testAdmin() {

        return "ADMIN access successful";
    }
}