package in.strikes.tms.user.dto;

import in.strikes.tms.user.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatuRequest(
        @NotNull(message = "Account status is required")
        AccountStatus status
) {
}
