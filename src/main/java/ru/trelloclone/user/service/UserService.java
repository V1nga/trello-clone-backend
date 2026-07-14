package ru.trelloclone.user.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.user.dto.UserResponse;
import ru.trelloclone.user.entity.User;
import ru.trelloclone.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MIN_SEARCH_QUERY_LENGTH = 2;

    private final UserRepository userRepository;

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
    }

    @Transactional
    public User updateDisplayName(UUID userId, String displayName) {
        User user = getById(userId);
        user.setDisplayName(displayName);

        return user;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(UUID currentUserId, String query, Pageable pageable) {
        String trimmedQuery = query == null ? "" : query.trim();
        if (trimmedQuery.length() < MIN_SEARCH_QUERY_LENGTH) {
            throw ApiException.badRequest("Search query must be at least " + MIN_SEARCH_QUERY_LENGTH + " characters long");
        }

        return userRepository.search(currentUserId, trimmedQuery, pageable).map(UserResponse::from);
    }
}
