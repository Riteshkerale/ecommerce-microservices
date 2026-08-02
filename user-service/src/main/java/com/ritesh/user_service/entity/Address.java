package com.ritesh.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String street;

    private String city;

    private String state;

    private String country;

    private String postalCode;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}