package ru.trelloclone.attachment.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.attachment.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
}
