package ru.trelloclone.checklist.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.checklist.entity.Checklist;

public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {

    long countByCard_Id(UUID cardId);
}
