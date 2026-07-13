package ru.trelloclone.user.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.user.entity.User;
import ru.trelloclone.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

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
}
