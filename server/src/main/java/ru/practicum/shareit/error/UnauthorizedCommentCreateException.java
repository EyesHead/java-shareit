package ru.practicum.shareit.error;

public class UnauthorizedCommentCreateException extends RuntimeException {
    public UnauthorizedCommentCreateException(String s) {
        super(s);
    }
}