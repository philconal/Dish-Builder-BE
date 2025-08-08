package com.conal.dishbuilder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    private String username;
    private String password;
    private String logo_url;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private UUID tenantId;

}
