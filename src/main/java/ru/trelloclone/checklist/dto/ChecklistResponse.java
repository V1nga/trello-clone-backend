package ru.trelloclone.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ChecklistResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) String title,
    @Schema(requiredMode = REQUIRED) int position,
    @Schema(requiredMode = REQUIRED) List<ChecklistItemResponse> items
) {
}
