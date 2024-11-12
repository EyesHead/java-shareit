package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.entity.Comment;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentPostDto commentPostDto);

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toResponse(Comment comment);

    Collection<CommentResponseDto> toResponseList(Collection<Comment> comments);
}