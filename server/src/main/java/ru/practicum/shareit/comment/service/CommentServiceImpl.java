package ru.practicum.shareit.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.error.BookingNotFoundException;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UnauthorizedCommentCreateException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    UserRepository userRepository;

    @Transactional
    @Override
    public CommentResponseDto createComment(CommentPostDto commentPostDto, long authorId, long itemId) {
        final LocalDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"))
                .toLocalDateTime();

        log.debug("[SERVER | SERVICE] Start creating comment for itemId: {}, authorId: {}", itemId, authorId);
        // Проверяем автора
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));
        log.trace("[SERVER | SERVICE] Author of comment exists: {}", author);

        // Проверяем предмет
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        log.trace("[SERVER | SERVICE] Item for comment exists: {}", item);

        // Проверяем бронирование и пользователя оставляющего комментарий на то, что он является арендатором
        Booking booking = bookingRepository.findByItemIdAndBookerId(itemId, authorId)
                .orElseThrow(() -> new BookingNotFoundException(itemId, authorId));
        log.trace("[SERVER | SERVICE] Booking for comment exists: {}", booking);

        // Проверяем аренду что она действительно завершена
        BookingStatus bookingStatus = booking.getStatus();
        LocalDateTime endOfBooking = booking.getEnd();
        log.trace("[SERVER | SERVICE] Validate request on correct booking data for comment.\n" +
                "Expect: '{}' should be APPROVED and endOfBooking='{}' should be BEFORE then comment created at='{}'",
                bookingStatus, endOfBooking, now);
        if (!bookingStatus.equals(BookingStatus.APPROVED) || !endOfBooking.isBefore(now)) {
            throw new UnauthorizedCommentCreateException("Booking is either not approved or not yet completed.");
        }

        // Создаем комментарий
        Comment comment = commentMapper.toComment(commentPostDto);
        comment.setAuthor(author);
        comment.setCreated(now);
        comment.setItem(item);
        log.trace("[SERVER | SERVICE] Comment prepared to save in repository: {}", comment);

        // Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);
        log.info("[SERVER | SERVICE]Comment saved with ID: {}", savedComment.getId());

        // Возвращаем DTO
        CommentResponseDto responseDto = commentMapper.toResponse(savedComment);
        log.debug("Response DTO: {}", responseDto);

        log.info("Comment creation completed successfully for itemId: {}, authorId: {}", itemId, authorId);
        return responseDto;
    }
}
