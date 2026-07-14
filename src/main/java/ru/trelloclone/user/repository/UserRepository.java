package ru.trelloclone.user.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trelloclone.user.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        select u from User u
        where u.id != :currentUserId
          and (lower(u.email) like lower(concat('%', :query, '%'))
           or lower(u.displayName) like lower(concat('%', :query, '%')))
    """)
    Page<User> search(@Param("currentUserId") UUID currentUserId, @Param("query") String query, Pageable pageable);
}
