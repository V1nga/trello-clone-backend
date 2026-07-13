package ru.trelloclone.label.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import ru.trelloclone.label.entity.Label;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record LabelResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) UUID boardId,
    @Schema(requiredMode = REQUIRED) String name,
    @Schema(requiredMode = REQUIRED) String color
) {

    public static LabelResponse from(Label label) {
        return new LabelResponse(label.getId(), label.getBoard().getId(), label.getName(), label.getColor());
    }
}
