package ru.trelloclone.card.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trelloclone.card.dto.CardDetailResponse;
import ru.trelloclone.card.dto.CardSummaryResponse;
import ru.trelloclone.card.dto.CreateCardRequest;
import ru.trelloclone.card.dto.MoveCardRequest;
import ru.trelloclone.card.dto.UpdateCardRequest;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.service.CardService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Карточки: детали, перемещение, исполнители, метки")
public class CardController {

    private final CardService cardService;

    @PostMapping("/api/columns/{columnId}/cards")
    public ResponseEntity<CardSummaryResponse> createCard(@AuthenticationPrincipal UUID userId, @PathVariable UUID columnId, @Valid @RequestBody CreateCardRequest request) {
        Card card = cardService.createCard(columnId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new CardSummaryResponse(
                card.getId(),
                card.getColumn().getId(),
                card.getTitle(),
                card.getPosition(),
                card.getDueDate(),
                List.of(),
                List.of()
            )
        );
    }

    @GetMapping("/api/columns/{columnId}/cards")
    public List<CardSummaryResponse> listCards(@AuthenticationPrincipal UUID userId, @PathVariable UUID columnId) {
        return cardService.listCardsForColumn(columnId, userId);
    }

    @GetMapping("/api/boards/{boardId}/cards/search")
    public Page<CardSummaryResponse> searchCards(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @RequestParam String query, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return cardService.searchCards(boardId, userId, query, pageable);
    }

    @GetMapping("/api/boards/{boardId}/cards/archived")
    public List<CardSummaryResponse> listArchivedCards(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        return cardService.listArchivedCards(boardId, userId);
    }

    @GetMapping("/api/cards/{cardId}")
    public CardDetailResponse getCard(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId) {
        return cardService.getCardDetail(cardId, userId);
    }

    @PatchMapping("/api/cards/{cardId}")
    public CardDetailResponse updateCard(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @Valid @RequestBody UpdateCardRequest request) {
        cardService.updateCard(cardId, userId, request);

        return cardService.getCardDetail(cardId, userId);
    }

    @PostMapping("/api/cards/{cardId}/move")
    public CardDetailResponse moveCard(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @Valid @RequestBody MoveCardRequest request) {
        cardService.moveCard(cardId, userId, request);

        return cardService.getCardDetail(cardId, userId);
    }

    @PostMapping("/api/cards/{cardId}/archive")
    public ResponseEntity<Void> archiveCard(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId) {
        cardService.archiveCard(cardId, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/cards/{cardId}/unarchive")
    public ResponseEntity<Void> unarchiveCard(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId) {
        cardService.unarchiveCard(cardId, userId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/cards/{cardId}/assignees/{assigneeUserId}")
    public ResponseEntity<Void> addAssignee(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @PathVariable UUID assigneeUserId) {
        cardService.addAssignee(cardId, userId, assigneeUserId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/cards/{cardId}/assignees/{assigneeUserId}")
    public ResponseEntity<Void> removeAssignee(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @PathVariable UUID assigneeUserId) {
        cardService.removeAssignee(cardId, userId, assigneeUserId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/cards/{cardId}/labels/{labelId}")
    public ResponseEntity<Void> addLabel(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @PathVariable UUID labelId) {
        cardService.addLabel(cardId, userId, labelId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/cards/{cardId}/labels/{labelId}")
    public ResponseEntity<Void> removeLabel(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @PathVariable UUID labelId) {
        cardService.removeLabel(cardId, userId, labelId);

        return ResponseEntity.noContent().build();
    }
}
