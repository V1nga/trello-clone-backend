package ru.trelloclone.board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.trelloclone.board.entity.BoardRole;

public record InviteMemberRequest(
    @NotBlank @Email String email,
    @NotNull BoardRole role
) {
}
