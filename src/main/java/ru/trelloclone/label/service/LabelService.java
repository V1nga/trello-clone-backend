package ru.trelloclone.label.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trelloclone.board.entity.Board;
import ru.trelloclone.board.service.BoardAccessService;
import ru.trelloclone.common.error.ApiException;
import ru.trelloclone.label.dto.CreateLabelRequest;
import ru.trelloclone.label.entity.Label;
import ru.trelloclone.label.repository.LabelRepository;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final BoardAccessService boardAccessService;

    @Transactional
    public Label createLabel(UUID boardId, UUID userId, CreateLabelRequest request) {
        Board board = boardAccessService.requireEditAccess(boardId, userId);

        Label label = new Label();
        label.setBoard(board);
        label.setName(request.name());
        label.setColor(request.color());

        return labelRepository.save(label);
    }

    public List<Label> listLabels(UUID boardId, UUID userId) {
        boardAccessService.requireViewAccess(boardId, userId);

        return labelRepository.findByBoard_Id(boardId);
    }

    @Transactional
    public void deleteLabel(UUID labelId, UUID userId) {
        Label label = labelRepository.findById(labelId).orElseThrow(() -> ApiException.notFound("Label not found"));
        boardAccessService.requireEditAccess(label.getBoard().getId(), userId);
        labelRepository.delete(label);
    }
}
