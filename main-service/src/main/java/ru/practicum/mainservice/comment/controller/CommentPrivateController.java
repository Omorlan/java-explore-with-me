package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.comment.dto.CommentDtoOut;
import ru.practicum.mainservice.comment.dto.NewCommentDto;
import ru.practicum.mainservice.comment.service.PrivateCommentService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class CommentPrivateController {

    private final PrivateCommentService commentPrivateService;

    @PostMapping("/{eventId}/comments/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoOut addComment(@RequestBody NewCommentDto newCommentDto,
                                    @PathVariable Long eventId,
                                    @PathVariable Long userId) {
        return commentPrivateService.addComment(newCommentDto, eventId, userId);
    }

    @DeleteMapping("/comments/{eventId}/{commentId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId, @PathVariable Long commentId,
                              @PathVariable Long userId) {
        commentPrivateService.deleteComment(eventId, commentId, userId);
    }
}
