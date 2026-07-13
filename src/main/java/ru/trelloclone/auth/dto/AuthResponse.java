package ru.trelloclone.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.trelloclone.user.dto.UserResponse;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AuthResponse(
    @Schema(requiredMode = REQUIRED) String accessToken,
    @Schema(requiredMode = REQUIRED) UserResponse user
) {
}
