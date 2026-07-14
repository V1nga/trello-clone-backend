package ru.trelloclone.boardcolumn.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.boardcolumn.entity.BoardColumn;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, UUID> {

    List<BoardColumn> findByBoard_IdAndArchivedFalseOrderByPosition(UUID boardId);

    long countByBoard_IdAndArchivedFalse(UUID boardId);

    List<BoardColumn> findByBoard_IdAndArchivedTrueOrderByArchivedAtDesc(UUID boardId);
}
