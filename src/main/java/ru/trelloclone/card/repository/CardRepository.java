package ru.trelloclone.card.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.card.entity.Card;

public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByColumn_IdAndArchivedFalseOrderByPosition(UUID columnId);

    long countByColumn_IdAndArchivedFalse(UUID columnId);

    List<Card> findByColumn_IdAndArchivedFalse(UUID columnId);
}
