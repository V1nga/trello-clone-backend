package ru.trelloclone.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trelloclone.board.entity.Board;
import ru.trelloclone.board.entity.BoardRole;
import ru.trelloclone.board.repository.BoardMemberRepository;
import ru.trelloclone.board.repository.BoardRepository;
import ru.trelloclone.common.error.ApiException;

@Service
@RequiredArgsConstructor
public class BoardAccessService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;

    public Board requireBoard(UUID boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> ApiException.notFound("Board not found"));
    }

    public Board requireViewAccess(UUID boardId, UUID userId) {
        Board board = requireBoard(boardId);
        if (!hasAccess(board, userId)) {
            throw ApiException.forbidden("You do not have access to this board");
        }

        return board;
    }

    public Board requireEditAccess(UUID boardId, UUID userId) {
        Board board = requireBoard(boardId);
        if (!canEdit(board, userId)) {
            throw ApiException.forbidden("You do not have edit access to this board");
        }

        return board;
    }

    public Board requireOwner(UUID boardId, UUID userId) {
        Board board = requireBoard(boardId);
        if (!isOwner(board, userId)) {
            throw ApiException.forbidden("Only the board owner can perform this action");
        }

        return board;
    }

    public boolean isOwner(Board board, UUID userId) {
        return board.getOwner().getId().equals(userId);
    }

    public boolean hasAccess(Board board, UUID userId) {
        return isOwner(board, userId) || boardMemberRepository.existsByBoard_IdAndUser_Id(board.getId(), userId);
    }

    public boolean canEdit(Board board, UUID userId) {
        if (isOwner(board, userId)) {
            return true;
        }

        return boardMemberRepository.findByBoard_IdAndUser_Id(board.getId(), userId)
                .map(member -> member.getRole() == BoardRole.EDITOR).orElse(false);
    }
}
