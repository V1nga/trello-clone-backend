package ru.trelloclone.boardcolumn.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.board.entity.Board;
import ru.trelloclone.board.service.BoardAccessService;
import ru.trelloclone.boardcolumn.dto.CreateColumnRequest;
import ru.trelloclone.boardcolumn.dto.MoveColumnRequest;
import ru.trelloclone.boardcolumn.dto.RenameColumnRequest;
import ru.trelloclone.boardcolumn.entity.BoardColumn;
import ru.trelloclone.boardcolumn.repository.BoardColumnRepository;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.repository.CardRepository;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final BoardColumnRepository columnRepository;
    private final BoardAccessService boardAccessService;
    private final ColumnAccessService columnAccessService;
    private final CardRepository cardRepository;

    @Transactional
    public BoardColumn createColumn(UUID boardId, UUID userId, CreateColumnRequest request) {
        Board board = boardAccessService.requireEditAccess(boardId, userId);

        long count = columnRepository.countByBoard_IdAndArchivedFalse(boardId);
        BoardColumn column = new BoardColumn();
        column.setBoard(board);
        column.setName(request.name());
        column.setPosition((int) count);

        return columnRepository.save(column);
    }

    public List<BoardColumn> listColumns(UUID boardId, UUID userId) {
        boardAccessService.requireViewAccess(boardId, userId);

        return columnRepository.findByBoard_IdAndArchivedFalseOrderByPosition(boardId);
    }

    public List<BoardColumn> listArchivedColumns(UUID boardId, UUID userId) {
        boardAccessService.requireViewAccess(boardId, userId);

        return columnRepository.findByBoard_IdAndArchivedTrueOrderByArchivedAtDesc(boardId);
    }

    @Transactional
    public BoardColumn renameColumn(UUID columnId, UUID userId, RenameColumnRequest request) {
        BoardColumn column = columnAccessService.requireColumnEditAccess(columnId, userId);
        column.setName(request.name());

        return column;
    }

    @Transactional
    public BoardColumn moveColumn(UUID columnId, UUID userId, MoveColumnRequest request) {
        BoardColumn column = columnAccessService.requireColumnEditAccess(columnId, userId);

        List<BoardColumn> columns = columnRepository.findByBoard_IdAndArchivedFalseOrderByPosition(column.getBoard().getId());
        columns.remove(column);

        int targetIndex = Math.clamp(request.position(), 0, columns.size());
        columns.add(targetIndex, column);

        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setPosition(i);
        }

        return column;
    }

    @Transactional
    public void archiveColumn(UUID columnId, UUID userId) {
        BoardColumn column = columnAccessService.requireEditAccessAllowingArchived(columnId, userId);
        column.setArchived(true);
        column.setArchivedAt(Instant.now());

        Instant now = Instant.now();
        for (Card card : cardRepository.findByColumn_IdAndArchivedFalse(columnId)) {
            card.setArchived(true);
            card.setUpdatedAt(now);
        }
    }

    @Transactional
    public BoardColumn unarchiveColumn(UUID columnId, UUID userId) {
        BoardColumn column = columnAccessService.requireEditAccessAllowingArchived(columnId, userId);
        column.setArchived(false);
        column.setArchivedAt(null);
        column.setPosition((int) columnRepository.countByBoard_IdAndArchivedFalse(column.getBoard().getId()));

        return column;
    }
}
