package ru.practicum.mainservice.comment.service;

import ru.practicum.mainservice.comment.dto.CommentDtoOut;
import ru.practicum.mainservice.comment.dto.NewCommentDto;

public interface PrivateCommentService {
    CommentDtoOut addComment(NewCommentDto newCommentDto, Long eventId, Long userId);

    void deleteComment(Long eventId, Long commentId, Long uerId);
}