package ru.trelloclone.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import ru.trelloclone.board.entity.BoardRole;
import ru.trelloclone.user.entity.User;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BoardMemberResponse(
    @Schema(requiredMode = REQUIRED) UUID userId,
    @Schema(requiredMode = REQUIRED) String email,
    @Schema(requiredMode = REQUIRED) String displayName,
    @Schema(requiredMode = REQUIRED, allowableValues = {"OWNER", "VIEWER", "EDITOR"}) String role
) {

    public static BoardMemberResponse owner(User user) {
        return new BoardMemberResponse(user.getId(), user.getEmail(), user.getDisplayName(), "OWNER");
    }

    public static BoardMemberResponse member(User user, BoardRole role) {
        return new BoardMemberResponse(user.getId(), user.getEmail(), user.getDisplayName(), role.name());
    }
}
