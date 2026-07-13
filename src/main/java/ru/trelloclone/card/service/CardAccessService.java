package ru.trelloclone.card.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trelloclone.board.service.BoardAccessService;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.repository.CardRepository;
import ru.trelloclone.common.error.ApiException;

@Service
@RequiredArgsConstructor
public class CardAccessService {

    private final CardRepository cardRepository;
    private final BoardAccessService boardAccessService;

    public Card requireCard(UUID cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> ApiException.notFound("Card not found"));
    }

    public Card requireCardViewAccess(UUID cardId, UUID userId) {
        Card card = requireCard(cardId);
        boardAccessService.requireViewAccess(boardIdOf(card), userId);

        return card;
    }

    public Card requireCardEditAccess(UUID cardId, UUID userId) {
        Card card = requireCard(cardId);
        boardAccessService.requireEditAccess(boardIdOf(card), userId);

        return card;
    }

    public UUID boardIdOf(Card card) {
        return card.getColumn().getBoard().getId();
    }
}
