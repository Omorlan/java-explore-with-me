package ru.practicum.mainservice.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.dto.CommentDtoOut;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.event.model.Event;

import java.util.List;

@Service
@AllArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {
    private final EntityFinderService entityFinderService;
    private final CommentRepository commentRepository;


    @Override
    public List<CommentDtoOut> getCommentsOfEvent(Long eventId, Pageable pageable) {
        Event event = entityFinderService.findEventById(eventId);
        List<Comment> comments = commentRepository.findByEvent(event, pageable);
        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .toList();
    }


}
