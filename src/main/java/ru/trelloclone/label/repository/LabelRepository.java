package ru.trelloclone.label.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.label.entity.Label;

public interface LabelRepository extends JpaRepository<Label, UUID> {

    List<Label> findByBoard_Id(UUID boardId);
}
