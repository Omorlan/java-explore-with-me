package ru.practicum.mainservice.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.dto.CommentDtoOut;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {


    private final EntityFinderService entityFinderService;
    private final CommentRepository commentRepository;

    @Override
    public void deleteCommentById(Long commentId) {
        Comment comment = entityFinderService.findCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDtoOut> getCommentsOfUser(Long userId, Pageable pageable) {
        User user = entityFinderService.findUserById(userId);
        List<Comment> comments = commentRepository.findByUser(user, pageable);
        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .toList();
    }

}
