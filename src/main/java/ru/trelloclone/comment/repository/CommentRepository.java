package ru.trelloclone.comment.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findByCard_IdOrderByCreatedAtDesc(UUID cardId, Pageable pageable);
}
