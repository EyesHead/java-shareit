package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.config.Constants;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("{itemId}/comment")
    public CommentResponseDto createCommentByRenter(@PathVariable("itemId") long itemId,
                                                    @RequestHeader(Constants.USER_ID_HEADER) long authorId,
                                                    @RequestBody CommentPostDto commentPostDto) {
        return commentService.createComment(commentPostDto, authorId, itemId);
    }
}
