package ru.trelloclone.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ru.trelloclone.label.dto.LabelResponse;
import ru.trelloclone.user.dto.UserResponse;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CardSummaryResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) UUID columnId,
    @Schema(requiredMode = REQUIRED) String title,
    @Schema(requiredMode = REQUIRED) int position,
    @Schema(nullable = true) Instant dueDate,
    @Schema(requiredMode = REQUIRED) List<LabelResponse> labels,
    @Schema(requiredMode = REQUIRED) List<UserResponse> assignees
) {
}
