package ru.trelloclone.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import ru.trelloclone.board.entity.Board;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BoardResponse(
    @Schema(requiredMode = REQUIRED) UUID id,
    @Schema(requiredMode = REQUIRED) String name,
    @Schema(nullable = true) String description,
    @Schema(requiredMode = REQUIRED) UUID ownerId,
    @Schema(requiredMode = REQUIRED) Instant createdAt
) {

    public static BoardResponse from(Board board) {
        return new BoardResponse(
            board.getId(),
            board.getName(),
            board.getDescription(),
            board.getOwner().getId(),
            board.getCreatedAt()
        );
    }
}
