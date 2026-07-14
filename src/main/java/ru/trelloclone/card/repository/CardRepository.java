package ru.trelloclone.card.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trelloclone.card.entity.Card;

public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByColumn_IdAndArchivedFalseOrderByPosition(UUID columnId);

    long countByColumn_IdAndArchivedFalse(UUID columnId);

    List<Card> findByColumn_IdAndArchivedFalse(UUID columnId);

    List<Card> findByColumn_Board_IdAndAssignees_IdAndArchivedFalse(UUID boardId, UUID userId);

    @Query("""
        select c from Card c
        where c.column.board.id = :boardId
          and c.archived = false
          and (lower(c.title) like lower(concat('%', :query, '%'))
           or lower(c.description) like lower(concat('%', :query, '%')))
    """)
    Page<Card> search(@Param("boardId") UUID boardId, @Param("query") String query, Pageable pageable);
}
