package ru.trelloclone.label.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLabelRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 20) String color
) {
}
