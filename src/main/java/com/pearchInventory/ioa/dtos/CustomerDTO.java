package com.pearchInventory.ioa.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {
}