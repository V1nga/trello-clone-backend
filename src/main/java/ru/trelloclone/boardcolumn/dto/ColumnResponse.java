package ru.trelloclone.boardcolumn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import ru.trelloclone.boardcolumn.entity.BoardColumn;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ColumnResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) UUID boardId,
    @Schema(requiredMode = REQUIRED) String name,
    @Schema(requiredMode = REQUIRED) int position,
    @Schema(requiredMode = REQUIRED) boolean archived
) {

    public static ColumnResponse from(BoardColumn column) {
        return new ColumnResponse(
            column.getId(),
            column.getBoard().getId(),
            column.getName(),
            column.getPosition(),
            column.isArchived()
        );
    }
}
