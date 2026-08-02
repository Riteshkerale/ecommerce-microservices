package com.ritesh.user_service.dtos.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String phoneNumber;

    private String profileImage;

    private List<AddressDto> addresses;
}