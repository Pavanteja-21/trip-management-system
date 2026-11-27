package in.strikes.tms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        String fullName,

        @NotBlank(message = "Email is required")
        @Size(max = 150)
        @Email(message = "Invalid email format")
        String email
) {}
