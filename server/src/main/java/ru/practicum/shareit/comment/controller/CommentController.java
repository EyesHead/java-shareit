package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.util.Constants;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{itemId}/comment")
    public CommentResponseDto createCommentByRenter(@PathVariable("itemId") long itemId,
                                                    @RequestHeader(Constants.USER_ID_HEADER) long authorId,
                                                    @RequestBody CommentPostDto commentPostDto) {
        log.info("[SERVER | CONTROLLER] Received request to create a comment for itemId: {}, authorId: {}", itemId, authorId);
        log.debug("Comment details: {}", commentPostDto);

        CommentResponseDto response = commentService.createComment(commentPostDto, authorId, itemId);

        log.info("[SERVER | CONTROLLER] Successfully created comment with ID: {} for itemId: {}", response.getId(), itemId);
        return response;
    }
}

