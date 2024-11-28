package ru.practicum.shareit.comment.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServiceIntegrationTest {

    @Autowired
    CommentService commentService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateComment_Success() {
        // Arrange
        User author = userRepository.save(new User(null, "Test User", "test@example.com"));
        User owner = userRepository.save(new User(null, "Owner User", "owner@example.com"));

        Item item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .booker(author)
                .item(item)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        CommentPostDto commentPostDto = new CommentPostDto("This is a test comment");

        // Act
        CommentResponseDto response = commentService.createComment(commentPostDto, author.getId(), item.getId());

        // Assert
        assertNotNull(response);
        assertEquals("This is a test comment", response.getText());
        assertEquals("Test User", response.getAuthorName());
    }

    @Test
    void testCreateComment_UserNotFound() {
        // Arrange
        CommentPostDto commentPostDto = new CommentPostDto("This is a test comment");

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> commentService.createComment(commentPostDto, 999L, 1L));
    }

    @Test
    void testCreateComment_ItemNotFound() {
        // Arrange
        User author = userRepository.save(new User(null, "Test User", "test@example.com"));
        CommentPostDto commentPostDto = new CommentPostDto("This is a test comment");

        // Act & Assert
        assertThrows(ItemNotFoundException.class,
                () -> commentService.createComment(commentPostDto, author.getId(), 999L));
    }

    @Test
    void testCreateComment_BookingNotFound() {
        // Arrange
        User author = userRepository.save(new User(null, "Test User", "test@example.com"));
        User owner = userRepository.save(new User(null, "Owner User", "owner@example.com"));

        Item item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        CommentPostDto commentPostDto = new CommentPostDto("This is a test comment");

        // Act & Assert
        assertThrows(BookingNotFoundException.class,
                () -> commentService.createComment(commentPostDto, author.getId(), item.getId()));
    }

    @Test
    void testCreateComment_BookingNotCompleted() {
        // Arrange
        User author = userRepository.save(new User(null, "Test User", "test@example.com"));
        User owner = userRepository.save(new User(null, "Owner User", "owner@example.com"));

        Item item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        bookingRepository.save(Booking.builder()
                .booker(author)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.APPROVED)
                .build());

        CommentPostDto commentPostDto = new CommentPostDto("This is a test comment");

        // Act & Assert
        assertThrows(UnauthorizedCommentCreateException.class,
                () -> commentService.createComment(commentPostDto, author.getId(), item.getId()));
    }
}
