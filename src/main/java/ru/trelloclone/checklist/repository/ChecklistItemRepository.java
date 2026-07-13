package ru.trelloclone.checklist.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.checklist.entity.ChecklistItem;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {

    long countByChecklist_Id(UUID checklistId);
}
