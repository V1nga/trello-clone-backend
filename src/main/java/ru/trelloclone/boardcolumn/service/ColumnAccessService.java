package ru.trelloclone.boardcolumn.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trelloclone.board.service.BoardAccessService;
import ru.trelloclone.boardcolumn.entity.BoardColumn;
import ru.trelloclone.boardcolumn.repository.BoardColumnRepository;
import ru.trelloclone.common.error.ApiException;

@Service
@RequiredArgsConstructor
public class ColumnAccessService {

    private final BoardColumnRepository columnRepository;
    private final BoardAccessService boardAccessService;

    public BoardColumn requireColumn(UUID columnId) {
        return columnRepository.findById(columnId).orElseThrow(() -> ApiException.notFound("Column not found"));
    }

    public BoardColumn requireColumnViewAccess(UUID columnId, UUID userId) {
        BoardColumn column = requireColumn(columnId);
        boardAccessService.requireViewAccess(column.getBoard().getId(), userId);

        return column;
    }

    public BoardColumn requireColumnEditAccess(UUID columnId, UUID userId) {
        BoardColumn column = requireColumn(columnId);
        boardAccessService.requireEditAccess(column.getBoard().getId(), userId);

        return column;
    }
}
