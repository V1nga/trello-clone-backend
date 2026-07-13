package ru.trelloclone.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ru.trelloclone.attachment.dto.AttachmentResponse;
import ru.trelloclone.checklist.dto.ChecklistResponse;
import ru.trelloclone.label.dto.LabelResponse;
import ru.trelloclone.user.dto.UserResponse;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CardDetailResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) UUID columnId,
    @Schema(requiredMode = REQUIRED) UUID boardId,
    @Schema(requiredMode = REQUIRED) String title,
    @Schema(nullable = true) String description,
    @Schema(requiredMode = REQUIRED) int position,
    @Schema(requiredMode = REQUIRED) boolean archived,
    @Schema(nullable = true) Instant dueDate,
    @Schema(requiredMode = REQUIRED) Instant createdAt,
    @Schema(requiredMode = REQUIRED) Instant updatedAt,
    @Schema(requiredMode = REQUIRED) List<UserResponse> assignees,
    @Schema(requiredMode = REQUIRED) List<LabelResponse> labels,
    @Schema(requiredMode = REQUIRED) List<ChecklistResponse> checklists,
    @Schema(requiredMode = REQUIRED) List<AttachmentResponse> attachments
) {
}
