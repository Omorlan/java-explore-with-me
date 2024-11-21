package ru.practicum.mainservice.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.mainservice.comment.dto.CommentDtoOut;

import java.util.List;

public interface AdminCommentService {

    void deleteCommentById(Long commentId);

    List<CommentDtoOut> getCommentsOfUser(Long userId, Pageable pageable);
}
