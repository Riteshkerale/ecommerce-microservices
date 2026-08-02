package com.ritesh.user_service.dtos.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    private Long id;

    private String street;

    private String city;

    private String state;

    private String country;

    private String postalCode;
}