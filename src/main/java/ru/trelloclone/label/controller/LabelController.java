package ru.trelloclone.label.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.trelloclone.label.dto.CreateLabelRequest;
import ru.trelloclone.label.dto.LabelResponse;
import ru.trelloclone.label.entity.Label;
import ru.trelloclone.label.service.LabelService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Labels", description = "Метки доски")
public class LabelController {

    private final LabelService labelService;

    @PostMapping("/api/boards/{boardId}/labels")
    public ResponseEntity<LabelResponse> createLabel(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId, @Valid @RequestBody CreateLabelRequest request) {
        Label label = labelService.createLabel(boardId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(LabelResponse.from(label));
    }

    @GetMapping("/api/boards/{boardId}/labels")
    public List<LabelResponse> listLabels(@AuthenticationPrincipal UUID userId, @PathVariable UUID boardId) {
        return labelService.listLabels(boardId, userId).stream().map(LabelResponse::from).toList();
    }

    @DeleteMapping("/api/labels/{labelId}")
    public ResponseEntity<Void> deleteLabel(@AuthenticationPrincipal UUID userId, @PathVariable UUID labelId) {
        labelService.deleteLabel(labelId, userId);

        return ResponseEntity.noContent().build();
    }
}
