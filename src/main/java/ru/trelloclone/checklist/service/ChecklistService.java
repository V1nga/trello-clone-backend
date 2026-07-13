package ru.trelloclone.checklist.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.service.CardAccessService;
import ru.trelloclone.checklist.dto.ChecklistItemResponse;
import ru.trelloclone.checklist.dto.ChecklistResponse;
import ru.trelloclone.checklist.dto.CreateChecklistItemRequest;
import ru.trelloclone.checklist.dto.CreateChecklistRequest;
import ru.trelloclone.checklist.dto.UpdateChecklistItemRequest;
import ru.trelloclone.checklist.entity.Checklist;
import ru.trelloclone.checklist.entity.ChecklistItem;
import ru.trelloclone.checklist.repository.ChecklistItemRepository;
import ru.trelloclone.checklist.repository.ChecklistRepository;
import ru.trelloclone.common.error.ApiException;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final CardAccessService cardAccessService;

    @Transactional
    public Checklist createChecklist(UUID cardId, UUID userId, CreateChecklistRequest request) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);

        Checklist checklist = new Checklist();
        checklist.setCard(card);
        checklist.setTitle(request.title());
        checklist.setPosition((int) checklistRepository.countByCard_Id(cardId));

        return checklistRepository.save(checklist);
    }

    @Transactional
    public ChecklistItem addItem(UUID checklistId, UUID userId, CreateChecklistItemRequest request) {
        Checklist checklist = requireChecklist(checklistId);
        cardAccessService.requireCardEditAccess(checklist.getCard().getId(), userId);

        ChecklistItem item = new ChecklistItem();
        item.setChecklist(checklist);
        item.setText(request.text());
        item.setPosition((int) checklistItemRepository.countByChecklist_Id(checklistId));

        return checklistItemRepository.save(item);
    }

    @Transactional
    public ChecklistItem updateItem(UUID itemId, UUID userId, UpdateChecklistItemRequest request) {
        ChecklistItem item = checklistItemRepository.findById(itemId).orElseThrow(() -> ApiException.notFound("Checklist item not found"));
        Checklist checklist = item.getChecklist();
        cardAccessService.requireCardEditAccess(checklist.getCard().getId(), userId);

        item.setDone(request.done());

        return item;
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> listForCard(Card card) {
        return card.getChecklists().stream()
            .map(checklist ->
                new ChecklistResponse(
                    checklist.getId(),
                    checklist.getTitle(),
                    checklist.getPosition(),
                    checklist.getItems().stream().map(ChecklistItemResponse::from).toList()
                )
            )
            .toList();
    }

    private Checklist requireChecklist(UUID checklistId) {
        return checklistRepository.findById(checklistId).orElseThrow(() -> ApiException.notFound("Checklist not found"));
    }
}
