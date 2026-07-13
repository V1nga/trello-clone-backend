package ru.trelloclone.card.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MoveCardRequest(
    @NotNull UUID columnId,
    @NotNull @Min(0) Integer position
) {
}
