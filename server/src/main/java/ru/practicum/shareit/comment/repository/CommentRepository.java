package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}