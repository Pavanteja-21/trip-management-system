package in.strikes.tms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        String fullName,

        @NotBlank(message = "Email is required")
        @Size(max = 150)
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 & 72 characters")
        String password
) {}
