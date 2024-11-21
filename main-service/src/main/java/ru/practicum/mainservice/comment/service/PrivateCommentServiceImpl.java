package ru.practicum.mainservice.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.dto.CommentDtoOut;
import ru.practicum.mainservice.comment.dto.NewCommentDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.exception.exception.BadRequestException;
import ru.practicum.mainservice.exception.exception.ErrorCommentException;
import ru.practicum.mainservice.user.model.User;

@Service
@AllArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final EntityFinderService entityFinderService;
    private final CommentRepository commentRepository;

    @Override
    public CommentDtoOut addComment(NewCommentDto newCommentDto, Long eventId, Long userId) {
        User user = entityFinderService.findUserById(userId);
        Event event = entityFinderService.findEventById(eventId);
        if (newCommentDto.getText().isEmpty() || newCommentDto.getText() == null) {
            throw new BadRequestException("The text field is missing or empty.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new BadRequestException("Comments can only be added to published events.");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, user, event);
        commentRepository.save(comment);
        return CommentMapper.toCommentDtoOut(comment);
    }

    @Override
    public void deleteComment(Long eventId, Long commentId, Long userId) {
        entityFinderService.findUserById(userId);
        entityFinderService.findEventById(eventId);
        Comment comment = entityFinderService.findCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ErrorCommentException("Only the author can delete their comment.");
        }
        commentRepository.delete(comment);
    }

}
