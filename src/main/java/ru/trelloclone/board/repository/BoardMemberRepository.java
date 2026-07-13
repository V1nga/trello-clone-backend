package ru.trelloclone.board.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.trelloclone.board.entity.BoardMember;

public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {

    Optional<BoardMember> findByBoard_IdAndUser_Id(UUID boardId, UUID userId);

    List<BoardMember> findByBoard_Id(UUID boardId);

    boolean existsByBoard_IdAndUser_Id(UUID boardId, UUID userId);
}
