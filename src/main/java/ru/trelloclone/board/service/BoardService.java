package ru.trelloclone.board.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.board.dto.BoardMemberResponse;
import ru.trelloclone.board.dto.CreateBoardRequest;
import ru.trelloclone.board.dto.InviteMemberRequest;
import ru.trelloclone.board.dto.UpdateBoardRequest;
import ru.trelloclone.board.dto.UpdateMemberRoleRequest;
import ru.trelloclone.board.entity.Board;
import ru.trelloclone.board.entity.BoardMember;
import ru.trelloclone.board.repository.BoardMemberRepository;
import ru.trelloclone.board.repository.BoardRepository;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.user.entity.User;
import ru.trelloclone.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardAccessService boardAccessService;
    private final UserRepository userRepository;

    @Transactional
    public Board createBoard(UUID ownerId, CreateBoardRequest request) {
        Board board = new Board();
        board.setName(request.name());
        board.setDescription(request.description());
        board.setOwner(userRepository.getReferenceById(ownerId));

        return boardRepository.save(board);
    }

    public Page<Board> listMyBoards(UUID userId, Pageable pageable) {
        return boardRepository.findAccessibleByUserId(userId, pageable);
    }

    public Board getBoard(UUID boardId, UUID userId) {
        return boardAccessService.requireViewAccess(boardId, userId);
    }

    @Transactional
    public Board updateBoard(UUID boardId, UUID userId, UpdateBoardRequest request) {
        Board board = boardAccessService.requireOwner(boardId, userId);
        board.setName(request.name());
        board.setDescription(request.description());

        return board;
    }

    @Transactional
    public void deleteBoard(UUID boardId, UUID userId) {
        Board board = boardAccessService.requireOwner(boardId, userId);
        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public List<BoardMemberResponse> listMembers(UUID boardId, UUID userId) {
        Board board = boardAccessService.requireViewAccess(boardId, userId);

        List<BoardMemberResponse> members = boardMemberRepository.findByBoard_Id(boardId).stream()
            .map(member -> BoardMemberResponse.member(member.getUser(), member.getRole()))
            .toList();

        return Stream.concat(Stream.of(BoardMemberResponse.owner(board.getOwner())), members.stream()).toList();
    }

    @Transactional
    public BoardMemberResponse inviteMember(UUID boardId, UUID requesterId, InviteMemberRequest request) {
        Board board = boardAccessService.requireOwner(boardId, requesterId);

        User targetUser = userRepository.findByEmail(request.email()).orElseThrow(() -> ApiException.notFound("User with this email is not registered"));

        if (targetUser.getId().equals(board.getOwner().getId())) {
            throw ApiException.conflict("Board owner is already a participant");
        }
        if (boardMemberRepository.existsByBoard_IdAndUser_Id(boardId, targetUser.getId())) {
            throw ApiException.conflict("User is already a board member");
        }

        BoardMember member = new BoardMember();
        member.setBoard(board);
        member.setUser(targetUser);
        member.setRole(request.role());
        boardMemberRepository.save(member);

        return BoardMemberResponse.member(targetUser, member.getRole());
    }

    @Transactional
    public void removeMember(UUID boardId, UUID requesterId, UUID targetUserId) {
        boardAccessService.requireOwner(boardId, requesterId);

        BoardMember member = boardMemberRepository.findByBoard_IdAndUser_Id(boardId, targetUserId).orElseThrow(() -> ApiException.notFound("Board member not found"));
        boardMemberRepository.delete(member);
    }

    @Transactional
    public BoardMemberResponse updateMemberRole(UUID boardId, UUID requesterId, UUID targetUserId, UpdateMemberRoleRequest request) {
        boardAccessService.requireOwner(boardId, requesterId);

        BoardMember member = boardMemberRepository.findByBoard_IdAndUser_Id(boardId, targetUserId).orElseThrow(() -> ApiException.notFound("Board member not found"));
        member.setRole(request.role());

        return BoardMemberResponse.member(member.getUser(), member.getRole());
    }
}
