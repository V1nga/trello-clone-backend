package ru.trelloclone.checklist.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.trelloclone.checklist.dto.ChecklistItemResponse;
import ru.trelloclone.checklist.dto.ChecklistResponse;
import ru.trelloclone.checklist.dto.CreateChecklistItemRequest;
import ru.trelloclone.checklist.dto.CreateChecklistRequest;
import ru.trelloclone.checklist.dto.UpdateChecklistItemRequest;
import ru.trelloclone.checklist.entity.Checklist;
import ru.trelloclone.checklist.entity.ChecklistItem;
import ru.trelloclone.checklist.service.ChecklistService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Checklists", description = "Чек-листы карточек")
public class ChecklistController {

    private final ChecklistService checklistService;

    @PostMapping("/api/cards/{cardId}/checklists")
    public ResponseEntity<ChecklistResponse> createChecklist(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @Valid @RequestBody CreateChecklistRequest request) {
        Checklist checklist = checklistService.createChecklist(cardId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ChecklistResponse(checklist.getId(), checklist.getTitle(), checklist.getPosition(), List.of()));
    }

    @PostMapping("/api/checklists/{checklistId}/items")
    public ResponseEntity<ChecklistItemResponse> addItem(@AuthenticationPrincipal UUID userId, @PathVariable UUID checklistId, @Valid @RequestBody CreateChecklistItemRequest request) {
        ChecklistItem item = checklistService.addItem(checklistId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ChecklistItemResponse.from(item));
    }

    @PatchMapping("/api/checklist-items/{itemId}")
    public ChecklistItemResponse updateItem(@AuthenticationPrincipal UUID userId, @PathVariable UUID itemId, @Valid @RequestBody UpdateChecklistItemRequest request) {
        return ChecklistItemResponse.from(checklistService.updateItem(itemId, userId, request));
    }
}
