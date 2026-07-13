package ru.trelloclone.board.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trelloclone.board.dto.BoardMemberResponse;
import ru.trelloclone.board.dto.BoardResponse;
import ru.trelloclone.board.dto.CreateBoardRequest;
import ru.trelloclone.board.dto.InviteMemberRequest;
import ru.trelloclone.board.dto.UpdateBoardRequest;
import ru.trelloclone.board.dto.UpdateMemberRoleRequest;
import ru.trelloclone.board.entity.Board;
import ru.trelloclone.board.service.BoardService;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "Доски, участники и роли")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateBoardRequest request) {
        Board board = boardService.createBoard(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BoardResponse.from(board));
    }

    @GetMapping
    public Page<BoardResponse> listMyBoards(@AuthenticationPrincipal UUID userId, @PageableDefault(size = 20) Pageable pageable) {
        return boardService.listMyBoards(userId, pageable).map(BoardResponse::from);
    }

    @GetMapping("/{boardId}")
    public BoardResponse getBoard(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        return BoardResponse.from(boardService.getBoard(boardId, userId));
    }

    @PatchMapping("/{boardId}")
    public BoardResponse updateBoard(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @Valid @RequestBody UpdateBoardRequest request) {
        return BoardResponse.from(boardService.updateBoard(boardId, userId, request));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        boardService.deleteBoard(boardId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{boardId}/members")
    public List<BoardMemberResponse> listMembers(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        return boardService.listMembers(boardId, userId);
    }

    @PostMapping("/{boardId}/members")
    public ResponseEntity<BoardMemberResponse> inviteMember(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @Valid @RequestBody InviteMemberRequest request) {
        BoardMemberResponse response = boardService.inviteMember(boardId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{boardId}/members/{memberUserId}")
    public BoardMemberResponse updateMemberRole(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @PathVariable UUID memberUserId, @Valid @RequestBody UpdateMemberRoleRequest request) {
        return boardService.updateMemberRole(boardId, userId, memberUserId, request);
    }

    @DeleteMapping("/{boardId}/members/{memberUserId}")
    public ResponseEntity<Void> removeMember(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @PathVariable UUID memberUserId) {
        boardService.removeMember(boardId, userId, memberUserId);

        return ResponseEntity.noContent().build();
    }
}
