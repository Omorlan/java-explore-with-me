package ru.practicum.mainservice.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.comment.dto.CommentDtoOut;
import ru.practicum.mainservice.comment.dto.NewCommentDto;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;
@Component
public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .user(user)
                .build();
    }

    public static CommentDtoOut toCommentDtoOut(Comment comment) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .authorName(comment.getUser().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
