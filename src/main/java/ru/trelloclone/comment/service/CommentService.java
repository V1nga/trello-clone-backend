package ru.trelloclone.comment.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.service.CardAccessService;
import ru.trelloclone.comment.dto.CommentResponse;
import ru.trelloclone.comment.dto.CreateCommentRequest;
import ru.trelloclone.comment.entity.Comment;
import ru.trelloclone.comment.repository.CommentRepository;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.user.entity.User;
import ru.trelloclone.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CardAccessService cardAccessService;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(UUID cardId, UUID userId, CreateCommentRequest request) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);
        User author = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("Author not found"));

        Comment comment = new Comment();
        comment.setCard(card);
        comment.setAuthor(author);
        comment.setText(request.text());
        commentRepository.save(comment);

        return toResponse(comment, author.getDisplayName());
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> listComments(UUID cardId, UUID userId, Pageable pageable) {
        cardAccessService.requireCardViewAccess(cardId, userId);

        return commentRepository.findByCard_IdOrderByCreatedAtDesc(cardId, pageable)
            .map(comment -> toResponse(comment, comment.getAuthor().getDisplayName()));
    }

    private CommentResponse toResponse(Comment comment, String authorDisplayName) {
        return new CommentResponse(
            comment.getId(),
            comment.getCard().getId(),
            comment.getAuthor().getId(),
            authorDisplayName,
            comment.getText(),
            comment.getCreatedAt()
        );
    }
}
