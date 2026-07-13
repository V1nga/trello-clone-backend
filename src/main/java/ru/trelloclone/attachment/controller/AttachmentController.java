package ru.trelloclone.attachment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.trelloclone.attachment.dto.AttachmentResponse;
import ru.trelloclone.attachment.entity.Attachment;
import ru.trelloclone.attachment.service.AttachmentService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "Файловые вложения карточек")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(value = "/api/cards/{cardId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> uploadAttachment(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId, @RequestParam("file") MultipartFile file) {
        Attachment attachment = attachmentService.uploadAttachment(cardId, userId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(AttachmentResponse.from(attachment));
    }

    @GetMapping("/api/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@AuthenticationPrincipal UUID userId, @PathVariable UUID attachmentId) {
        Attachment attachment = attachmentService.requireForDownload(attachmentId, userId);
        Resource resource = attachmentService.loadAsResource(attachment);

        MediaType mediaType = attachment.getContentType() != null
            ? MediaType.parseMediaType(attachment.getContentType())
            : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/api/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@AuthenticationPrincipal UUID userId, @PathVariable UUID attachmentId) {
        attachmentService.deleteAttachment(attachmentId, userId);

        return ResponseEntity.noContent().build();
    }
}
