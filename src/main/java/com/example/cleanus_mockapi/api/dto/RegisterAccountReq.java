package com.example.cleanus_mockapi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterAccountReq(
        @NotBlank @Pattern(regexp = "^\\d{3}$", message = "bankCode must be 3 digits")
        String bankCode,
        @NotBlank @Pattern(regexp = "^\\d{6,20}$", message = "accountNumber must be 6~20 digits")
        String accountNumber,
        @NotBlank @Size(max = 50)
        String holderName
) {}