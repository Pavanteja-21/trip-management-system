package in.strikes.tms.user.dto;

import in.strikes.tms.user.entity.AccountStatus;
import in.strikes.tms.user.entity.Role;

public record UserResponse(
    Long id,

    String fullName,

    String email,

    Role role,

    AccountStatus status
) {}
