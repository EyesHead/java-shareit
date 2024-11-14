package ru.practicum.shareit.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
public class CommentServiceImpl implements CommentService {
    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    UserRepository userRepository;

    @Override
    public CommentResponseDto createComment(CommentPostDto commentPostDto, long authorId, long itemId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        Booking booking = bookingRepository.findByItemIdAndAndBookerId(itemId, authorId)
                .orElseThrow(() -> new BookingNotFoundException(itemId, authorId));

        // Проверка на окончание аренды
        if (!booking.getStatus().equals(BookingStatus.APPROVED) ||
                booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new InvalidBookingStatusException("The rental has not yet been completed or approved");
        }

        Comment comment = commentMapper.toComment(commentPostDto);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);

        // Сохраняем комментарий отдельно
        return commentMapper.toResponse(commentRepository.save(comment));
    }
}