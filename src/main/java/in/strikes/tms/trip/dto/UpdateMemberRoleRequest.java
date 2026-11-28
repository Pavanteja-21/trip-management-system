package in.strikes.tms.trip.dto;

import in.strikes.tms.trip.entity.TripMemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull(message = "Member role is required")
        TripMemberRole role
) {}
