package ru.trelloclone.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBoardRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 2000) String description
) {
}
