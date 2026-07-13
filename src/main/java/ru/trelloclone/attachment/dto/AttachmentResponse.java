package ru.trelloclone.attachment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import ru.trelloclone.attachment.entity.Attachment;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AttachmentResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) UUID cardId,
    @Schema(requiredMode = REQUIRED) String filename,
    @Schema(nullable = true) String contentType,
    @Schema(requiredMode = REQUIRED) long size,
    @Schema(requiredMode = REQUIRED) UUID uploadedBy,
    @Schema(requiredMode = REQUIRED) Instant uploadedAt
) {

    public static AttachmentResponse from(Attachment attachment) {
        return new AttachmentResponse(
            attachment.getId(),
            attachment.getCard().getId(),
            attachment.getFilename(),
            attachment.getContentType(),
            attachment.getSize(),
            attachment.getUploadedBy().getId(),
            attachment.getUploadedAt()
        );
    }
}
