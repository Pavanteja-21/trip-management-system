package in.strikes.tms.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresAt
) {}
