package ru.trelloclone.boardcolumn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoveColumnRequest(
    @NotNull @Min(0) Integer position
) {
}
