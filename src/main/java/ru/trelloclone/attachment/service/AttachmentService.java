package ru.trelloclone.attachment.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.trelloclone.attachment.dto.AttachmentResponse;
import ru.trelloclone.attachment.entity.Attachment;
import ru.trelloclone.attachment.repository.AttachmentRepository;
import ru.trelloclone.card.entity.Card;
import ru.trelloclone.card.service.CardAccessService;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CardAccessService cardAccessService;
    private final UserRepository userRepository;

    @Value("${app.attachments.storage-path}")
    private String storageRoot;

    @Transactional
    public Attachment uploadAttachment(UUID cardId, UUID userId, MultipartFile file) {
        Card card = cardAccessService.requireCardEditAccess(cardId, userId);

        if (file.isEmpty()) {
            throw ApiException.badRequest("Attachment file must not be empty");
        }

        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + "-" + originalFilename;

        try {
            Path cardDir = Path.of(storageRoot, cardId.toString());
            Files.createDirectories(cardDir);
            Path target = cardDir.resolve(storedFilename);
            file.transferTo(target);

            Attachment attachment = new Attachment();
            attachment.setCard(card);
            attachment.setFilename(originalFilename);
            attachment.setStoredPath(target.toString());
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            attachment.setUploadedBy(userRepository.getReferenceById(userId));

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store attachment", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> listForCard(Card card) {
        return card.getAttachments().stream().map(AttachmentResponse::from).toList();
    }

    public Attachment requireForDownload(UUID attachmentId, UUID userId) {
        Attachment attachment = requireAttachment(attachmentId);
        cardAccessService.requireCardViewAccess(attachment.getCard().getId(), userId);

        return attachment;
    }

    public Resource loadAsResource(Attachment attachment) {
        try {
            Resource resource = new UrlResource(Path.of(attachment.getStoredPath()).toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw ApiException.notFound("Attachment file is missing on disk");
            }

            return resource;
        } catch (java.net.MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional
    public void deleteAttachment(UUID attachmentId, UUID userId) {
        Attachment attachment = requireAttachment(attachmentId);
        cardAccessService.requireCardEditAccess(attachment.getCard().getId(), userId);

        try {
            Files.deleteIfExists(Path.of(attachment.getStoredPath()));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete attachment file", e);
        }

        attachmentRepository.delete(attachment);
    }

    private Attachment requireAttachment(UUID attachmentId) {
        return attachmentRepository.findById(attachmentId).orElseThrow(() -> ApiException.notFound("Attachment not found"));
    }

    private String sanitizeFilename(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }
        String name = Path.of(original).getFileName().toString();

        return name.replaceAll("[\\r\\n]", "");
    }
}
