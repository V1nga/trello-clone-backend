package ru.trelloclone.checklist.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateChecklistItemRequest(
    @NotNull Boolean done
) {
}
