package ru.practicum.shareit.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.error.BookingNotFoundException;
import ru.practicum.shareit.error.InvalidBookingStatusException;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

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

    @Override
    public CommentResponseDto createComment(CommentPostDto commentPostDto, long authorId, long itemId) {
        log.info("Start creating comment for itemId: {}, authorId: {}", itemId, authorId);

        log.info("Start validating a request");
        // Проверяем автора
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("User with id: {} not found", authorId);
                    return new UserNotFoundException(authorId);
                });
        log.debug("Author found: {}", author);

        // Проверяем предмет
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        log.debug("Item found: {}", item);

        // Проверяем бронирование
        Booking booking = bookingRepository.findByItemIdAndAndBookerId(itemId, authorId)
                .orElseThrow(() -> new BookingNotFoundException(itemId, authorId));
        log.debug("Booking found: {}", booking);

        // Проверяем статус аренды
        if (!booking.getStatus().equals(BookingStatus.APPROVED) ||
                booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new InvalidBookingStatusException("The rental has not yet been completed or approved");
        }
        log.info("Request validated successfully");

        // Создаем комментарий
        Comment comment = commentMapper.toComment(commentPostDto);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        log.debug("Comment created: {}", comment);

        // Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved with ID: {}", savedComment.getId());

        // Возвращаем DTO
        CommentResponseDto responseDto = commentMapper.toResponse(savedComment);
        log.debug("Response DTO: {}", responseDto);

        log.info("Comment creation completed successfully for itemId: {}, authorId: {}", itemId, authorId);
        return responseDto;
    }
}
