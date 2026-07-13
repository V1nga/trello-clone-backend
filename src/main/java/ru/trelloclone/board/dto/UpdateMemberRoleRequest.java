package ru.trelloclone.board.dto;

import jakarta.validation.constraints.NotNull;
import ru.trelloclone.board.entity.BoardRole;

public record UpdateMemberRoleRequest(
    @NotNull BoardRole role
) {
}
