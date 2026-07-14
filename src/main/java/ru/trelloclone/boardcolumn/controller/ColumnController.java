package ru.trelloclone.boardcolumn.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trelloclone.boardcolumn.dto.ColumnResponse;
import ru.trelloclone.boardcolumn.dto.CreateColumnRequest;
import ru.trelloclone.boardcolumn.dto.MoveColumnRequest;
import ru.trelloclone.boardcolumn.dto.RenameColumnRequest;
import ru.trelloclone.boardcolumn.entity.BoardColumn;
import ru.trelloclone.boardcolumn.service.ColumnService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Columns", description = "Колонки доски")
public class ColumnController {

    private final ColumnService columnService;

    @PostMapping("/api/boards/{boardId}/columns")
    public ResponseEntity<ColumnResponse> createColumn(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @Valid @RequestBody CreateColumnRequest request) {
        BoardColumn column = columnService.createColumn(boardId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ColumnResponse.from(column));
    }

    @GetMapping("/api/boards/{boardId}/columns")
    public List<ColumnResponse> listColumns(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        return columnService.listColumns(boardId, userId).stream().map(ColumnResponse::from).toList();
    }

    @GetMapping("/api/boards/{boardId}/columns/archived")
    public List<ColumnResponse> listArchivedColumns(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        return columnService.listArchivedColumns(boardId, userId).stream().map(ColumnResponse::from).toList();
    }

    @PatchMapping("/api/columns/{columnId}")
    public ColumnResponse renameColumn(@AuthenticationPrincipal UUID userId, @PathVariable UUID columnId, @Valid @RequestBody RenameColumnRequest request) {
        return ColumnResponse.from(columnService.renameColumn(columnId, userId, request));
    }

    @PostMapping("/api/columns/{columnId}/move")
    public ColumnResponse moveColumn(@AuthenticationPrincipal UUID userId, @PathVariable UUID columnId, @Valid @RequestBody MoveColumnRequest request) {
        return ColumnResponse.from(columnService.moveColumn(columnId, userId, request));
    }

    @PostMapping("/api/columns/{columnId}/archive")
    public ResponseEntity<Void> archiveColumn(@AuthenticationPrincipal UUID userId, @PathVariable UUID columnId) {
        columnService.archiveColumn(columnId, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/columns/{columnId}/unarchive")
    public ColumnResponse unarchiveColumn(@AuthenticationPrincipal UUID userId, @PathVariable UUID columnId) {
        return ColumnResponse.from(columnService.unarchiveColumn(columnId, userId));
    }
}
