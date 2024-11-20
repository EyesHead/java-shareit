package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

public interface CommentService {
    CommentResponseDto createComment(CommentPostDto commentPostDto, long authorId, long itemId);
}