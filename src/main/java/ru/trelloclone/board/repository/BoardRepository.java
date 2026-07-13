package ru.trelloclone.board.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trelloclone.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, UUID> {

    @Query("""
            select b from Board b
            where b.owner.id = :userId
               or b.id in (select bm.board.id from BoardMember bm where bm.user.id = :userId)
            """)
    Page<Board> findAccessibleByUserId(@Param("userId") UUID userId, Pageable pageable);
}
