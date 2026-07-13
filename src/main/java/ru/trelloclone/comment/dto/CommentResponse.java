package ru.trelloclone.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CommentResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) UUID cardId,
    @Schema(requiredMode = REQUIRED) UUID authorId,
    @Schema(requiredMode = REQUIRED) String authorDisplayName,
    @Schema(requiredMode = REQUIRED) String text,
    @Schema(requiredMode = REQUIRED) Instant createdAt
) {
}
