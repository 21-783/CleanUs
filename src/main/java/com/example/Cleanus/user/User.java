package com.example.Cleanus.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users", indexes = @Index(name="idx_user_email", columnList = "email", unique = true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank
    @Size(min=60, max = 100)
    @Column(nullable = false, length = 200)
    private String passwordHash;

    private Instant createdAt;
}
