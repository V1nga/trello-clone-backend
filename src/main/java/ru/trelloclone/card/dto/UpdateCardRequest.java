package ru.trelloclone.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record UpdateCardRequest(
    @NotBlank @Size(max = 255) String title,
    @Size(max = 10000) String description,
    Instant dueDate
) {
}
