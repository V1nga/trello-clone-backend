package ru.trelloclone.board.listener;

import jakarta.persistence.PreRemove;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.trelloclone.board.entity.BoardMember;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.repository.CardRepository;

@Component
public class BoardMemberAssigneeCleanupListener {

    private final CardRepository cardRepository;

    public BoardMemberAssigneeCleanupListener(@Lazy CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @PreRemove
    public void onRemove(BoardMember member) {
        List<Card> assignedCards = cardRepository
            .findByColumn_Board_IdAndAssignees_IdAndArchivedFalse(member.getBoard().getId(), member.getUser().getId());
        assignedCards.forEach(card ->
            card.getAssignees().removeIf(user -> user.getId().equals(member.getUser().getId()))
        );
    }
}
