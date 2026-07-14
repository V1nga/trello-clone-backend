package ru.trelloclone.card.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.attachment.service.AttachmentService;
import ru.trelloclone.board.entity.Board;
import ru.trelloclone.board.service.BoardAccessService;
import ru.trelloclone.boardcolumn.entity.BoardColumn;
import ru.trelloclone.boardcolumn.service.ColumnAccessService;
import ru.trelloclone.card.dto.CardDetailResponse;
import ru.trelloclone.card.dto.CardSummaryResponse;
import ru.trelloclone.card.dto.CreateCardRequest;
import ru.trelloclone.card.dto.MoveCardRequest;
import ru.trelloclone.card.dto.UpdateCardRequest;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.repository.CardRepository;
import ru.trelloclone.checklist.service.ChecklistService;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.label.dto.LabelResponse;
import ru.trelloclone.label.entity.Label;
import ru.trelloclone.label.repository.LabelRepository;
import ru.trelloclone.user.dto.UserResponse;
import ru.trelloclone.user.entity.User;
import ru.trelloclone.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final int MIN_SEARCH_QUERY_LENGTH = 2;

    private final CardRepository cardRepository;
    private final CardAccessService cardAccessService;
    private final ColumnAccessService columnAccessService;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final BoardAccessService boardAccessService;
    private final ChecklistService checklistService;
    private final AttachmentService attachmentService;

    @Transactional
    public Card createCard(UUID columnId, UUID userId, CreateCardRequest request) {
        BoardColumn column = columnAccessService.requireColumnEditAccess(columnId, userId);

        Card card = new Card();
        card.setColumn(column);
        card.setTitle(request.title());
        card.setPosition((int) cardRepository.countByColumn_IdAndArchivedFalse(columnId));

        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<CardSummaryResponse> listCardsForColumn(UUID columnId, UUID userId) {
        columnAccessService.requireColumnViewAccess(columnId, userId);

        return cardRepository.findByColumn_IdAndArchivedFalseOrderByPosition(columnId).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public Page<CardSummaryResponse> searchCards(UUID boardId, UUID userId, String query, Pageable pageable) {
        boardAccessService.requireViewAccess(boardId, userId);

        String trimmedQuery = query == null ? "" : query.trim();
        if (trimmedQuery.length() < MIN_SEARCH_QUERY_LENGTH) {
            throw ApiException.badRequest("Search query must be at least " + MIN_SEARCH_QUERY_LENGTH + " characters long");
        }

        return cardRepository.search(boardId, trimmedQuery, pageable).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public CardDetailResponse getCardDetail(UUID cardId, UUID userId) {
        Card card = cardAccessService.requireCardViewAccess(cardId, userId);
        UUID boardId = cardAccessService.boardIdOf(card);

        return new CardDetailResponse(
            card.getId(),
            card.getColumn().getId(),
            boardId,
            card.getTitle(),
            card.getDescription(),
            card.getPosition(),
            card.isArchived(),
            card.getDueDate(),
            card.getCreatedAt(),
            card.getUpdatedAt(),
            assigneesOf(card),
            labelsOf(card),
            checklistService.listForCard(card),
            attachmentService.listForCard(card)
        );
    }

    @Transactional
    public void updateCard(UUID cardId, UUID userId, UpdateCardRequest request) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        card.setTitle(request.title());
        card.setDescription(request.description());
        card.setDueDate(request.dueDate());
        card.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void moveCard(UUID cardId, UUID userId, MoveCardRequest request) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        UUID sourceColumnId = card.getColumn().getId();
        UUID targetColumnId = request.columnId();

        BoardColumn sourceColumn = columnAccessService.requireColumn(sourceColumnId);
        BoardColumn targetColumn = columnAccessService.requireColumn(targetColumnId);
        if (!sourceColumn.getBoard().getId().equals(targetColumn.getBoard().getId())) {
            throw ApiException.badRequest("Cannot move a card to a column on a different board");
        }

        if (sourceColumnId.equals(targetColumnId)) {
            List<Card> columnCards = new ArrayList<>(cardRepository.findByColumn_IdAndArchivedFalseOrderByPosition(sourceColumnId));
            columnCards.remove(card);
            int targetIndex = clamp(request.position(), columnCards.size());
            columnCards.add(targetIndex, card);
            reindex(columnCards);
        } else {
            List<Card> sourceCards = new ArrayList<>(cardRepository.findByColumn_IdAndArchivedFalseOrderByPosition(sourceColumnId));
            sourceCards.remove(card);
            reindex(sourceCards);

            List<Card> targetCards = new ArrayList<>(cardRepository.findByColumn_IdAndArchivedFalseOrderByPosition(targetColumnId));
            int targetIndex = clamp(request.position(), targetCards.size());
            targetCards.add(targetIndex, card);
            card.setColumn(targetColumn);
            reindex(targetCards);
        }

        card.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void archiveCard(UUID cardId, UUID userId) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        card.setArchived(true);
        card.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void addAssignee(UUID cardId, UUID userId, UUID assigneeUserId) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        Board board = card.getColumn().getBoard();

        if (!boardAccessService.hasAccess(board, assigneeUserId)) {
            throw ApiException.badRequest("User is not a participant of this board");
        }

        User assignee = userRepository.getReferenceById(assigneeUserId);
        card.getAssignees().add(assignee);
    }

    @Transactional
    public void removeAssignee(UUID cardId, UUID userId, UUID assigneeUserId) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        card.getAssignees().removeIf(user -> user.getId().equals(assigneeUserId));
    }

    @Transactional
    public void addLabel(UUID cardId, UUID userId, UUID labelId) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        Label label = labelRepository.findById(labelId).orElseThrow(() -> ApiException.notFound("Label not found"));

        if (!label.getBoard().getId().equals(cardAccessService.boardIdOf(card))) {
            throw ApiException.badRequest("Label does not belong to this board");
        }
        card.getLabels().add(label);
    }

    @Transactional
    public void removeLabel(UUID cardId, UUID userId, UUID labelId) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        card.getLabels().removeIf(label -> label.getId().equals(labelId));
    }

    private CardSummaryResponse toSummary(Card card) {
        return new CardSummaryResponse(
            card.getId(),
            card.getColumn().getId(),
            card.getTitle(),
            card.getPosition(),
            card.getDueDate(),
            labelsOf(card),
            assigneesOf(card)
        );
    }

    private List<LabelResponse> labelsOf(Card card) {
        return card.getLabels().stream().map(LabelResponse::from).toList();
    }

    private List<UserResponse> assigneesOf(Card card) {
        return card.getAssignees().stream().map(UserResponse::from).toList();
    }

    private int clamp(int position, int size) {
        return Math.clamp(position, 0, size);
    }

    private void reindex(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }
    }
}
