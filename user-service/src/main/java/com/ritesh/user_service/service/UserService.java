package com.ritesh.user_service.service;

import com.ritesh.user_service.dtos.Request.UpdateUserRequest;
import com.ritesh.user_service.dtos.Request.UserRegisterRequest;
import com.ritesh.user_service.dtos.Response.AddressDto;
import com.ritesh.user_service.dtos.Response.UserResponse;
import com.ritesh.user_service.entity.Address;
import com.ritesh.user_service.entity.Role;
import com.ritesh.user_service.entity.User;
import com.ritesh.user_service.repository.UserRepository;
import com.ritesh.user_service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Register User
    public UserResponse registerUser(UserRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())   // Later -> passwordEncoder.encode(...)
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .enabled(true)
                .emailVerified(false)
                .build();

        // Create Address and connect it with User
        if (request.getAddresses() != null) {

            List<Address> addresses = request.getAddresses()
                    .stream()
                    .map(addressDto -> Address.builder()
                            .street(addressDto.getStreet())
                            .city(addressDto.getCity())
                            .state(addressDto.getState())
                            .country(addressDto.getCountry())
                            .postalCode(addressDto.getPostalCode())
                            .user(user)
                            .build())
                    .toList();

            user.getAddresses().addAll(addresses);
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    // Get User By Id
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return mapToResponse(user);
    }

    // Get All Users
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update User
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfileImage(request.getProfileImage());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    // Delete User
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    // Entity -> Response DTO
    private UserResponse mapToResponse(User user) {

        List<AddressDto> addressDtos = user.getAddresses()
                .stream()
                .map(address -> AddressDto.builder()
                        .id(address.getId())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .state(address.getState())
                        .country(address.getCountry())
                        .postalCode(address.getPostalCode())
                        .build())
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfileImage())
                .addresses(addressDtos)
                .build();
    }
}