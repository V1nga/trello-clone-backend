package ru.trelloclone.checklist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChecklistRequest(
    @NotBlank @Size(max = 255) String title
) {
}
