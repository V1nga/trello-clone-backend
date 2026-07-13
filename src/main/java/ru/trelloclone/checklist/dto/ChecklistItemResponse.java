package ru.trelloclone.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import ru.trelloclone.checklist.entity.ChecklistItem;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ChecklistItemResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) String text,
    @Schema(requiredMode = REQUIRED) boolean done,
    @Schema(requiredMode = REQUIRED) int position
) {

    public static ChecklistItemResponse from(ChecklistItem item) {
        return new ChecklistItemResponse(item.getId(), item.getText(), item.isDone(), item.getPosition());
    }
}
