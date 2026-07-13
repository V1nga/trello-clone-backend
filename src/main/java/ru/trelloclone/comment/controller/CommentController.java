package ru.trelloclone.comment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.trelloclone.comment.dto.CommentResponse;
import ru.trelloclone.comment.dto.CreateCommentRequest;
import ru.trelloclone.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Комментарии к карточкам")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/cards/{cardId}/comments")
    public ResponseEntity<CommentResponse> addComment(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(cardId, userId, request));
    }

    @GetMapping("/api/cards/{cardId}/comments")
    public Page<CommentResponse> listComments(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @PageableDefault(size = 20) Pageable pageable) {
        return commentService.listComments(cardId, userId, pageable);
    }
}
