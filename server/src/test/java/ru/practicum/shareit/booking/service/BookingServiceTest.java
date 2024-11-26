package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingFindStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.*;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Laptop")
                .description("High-end gaming laptop")
                .available(true)
                .owner(user)
                .build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        bookingResponseDto = new BookingResponseDto(1L, booking.getStart(), booking.getEnd(), BookingStatus.WAITING,
                new UserResponseDto(user.getId(), user.getName(), user.getEmail()),
                new ItemResponseDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable()));
    }

    @Test
    void createBooking_shouldReturnBookingResponseDto_whenBookingIsValid() {
        // Arrange
        Mockito.when(itemRepository.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingMapper.toBooking(bookingRequestDto)).thenReturn(booking);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Mockito.when(bookingMapper.toResponse(booking)).thenReturn(bookingResponseDto);

        // Act
        BookingResponseDto result = bookingService.createBooking(bookingRequestDto, user.getId());

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingResponseDto, result);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        item.setAvailable(false);
        Mockito.when(itemRepository.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(bookingRequestDto, user.getId()));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowUnavailableItemForBookingException_whenItemIsUnavailable() {
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        // Arrange
        item.setAvailable(false);
        Mockito.when(itemRepository.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(item));

        // Act & Assert
        Assertions.assertThrows(UnavailableItemForBookingException.class,
                () -> bookingService.createBooking(bookingRequestDto, user.getId()));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }

    @Test
    void approveBooking_shouldUpdateStatus_whenStatusIsValidAndOwnerApproves() {
        // Arrange
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(booking.getId(), user.getId()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Mockito.when(bookingMapper.toResponse(booking)).thenReturn(bookingResponseDto);

        // Act
        BookingResponseDto result = bookingService.approveBooking(booking.getId(), user.getId(), true);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(BookingStatus.APPROVED, booking.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void approveBooking_shouldThrowUnauthorizedUserApproveBookingException_whenUserIsNotOwner() {
        // Arrange
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(booking.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(UnauthorizedUserApproveBookingException.class,
                () -> bookingService.approveBooking(booking.getId(), user.getId(), true));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }


    @Test
    void approveBooking_shouldThrowInvalidBookingStatusForApprovingException_whenStatusIsNotWaiting() {
        // Arrange
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(booking.getId(), user.getId()))
                .thenReturn(Optional.of(booking));

        // Act & Assert
        Assertions.assertThrows(InvalidBookingStatusForApprovingException.class,
                () -> bookingService.approveBooking(booking.getId(), user.getId(), true));

        Mockito.verify(bookingRepository, Mockito.never()).save(any(Booking.class));
    }

    @Test
    void getBookingByIdAndUserId_shouldReturnBooking_whenBookingExists() {
        // Arrange
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(bookingMapper.toResponse(booking)).thenReturn(bookingResponseDto);

        // Act
        BookingResponseDto result = bookingService.getBookingByIdAndUserId(booking.getId(), user.getId());

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingResponseDto, result);
    }

    @Test
    void getBookingByIdAndUserId_shouldThrowBookingNotFoundException_whenBookingDoesNotExist() {
        // Arrange
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingByIdAndUserId(booking.getId(), user.getId()));

        Mockito.verify(bookingMapper, Mockito.never()).toResponse(any());
    }

    @Test
    void getAllByOwnerIdAndFindStatus_shouldReturnBookings_whenStatusIsCurrent() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findCurrentByItemOwnerId(
                        eq(user.getId()),
                        any(LocalDateTime.class),
                        eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Mockito.when(bookingRepository.findByItemOwnerId(user.getId())).thenReturn(List.of(booking));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByOwnerIdAndFindStatus(user.getId(), BookingFindStatus.CURRENT);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findCurrentByItemOwnerId(eq(user.getId()), any(LocalDateTime.class), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByOwnerIdAndFindStatus_shouldThrowUserNotFoundException_whenOwnerDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.existsById(eq(user.getId()))).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllByOwnerIdAndFindStatus(user.getId(), BookingFindStatus.CURRENT));

        Mockito.verify(bookingRepository, Mockito.never()).findByItemOwnerId(any(Long.class));
    }

    @Test
    void getAllByOwnerIdAndFindStatus_shouldReturnEmptyList_whenOwnerHasNoBookings() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerId(user.getId())).thenReturn(List.of(booking));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByOwnerIdAndFindStatus(user.getId(), BookingFindStatus.CURRENT);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(bookingRepository, Mockito.times(1)).findByItemOwnerId(user.getId());
    }

    @Test
    void getAllByOwnerIdAndFindStatus_shouldThrowsUnauthorizedUserGetBookingsException_whenRequesterIsNotOwner() {
        Mockito.when(userRepository.existsById(eq(user.getId()))).thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerId(eq(user.getId()))).thenReturn(List.of());

        Assertions.assertThrows(UnauthorizedUserGetBookingsException.class,
                () -> bookingService.getAllByOwnerIdAndFindStatus(user.getId(), BookingFindStatus.CURRENT));

        Mockito.verify(bookingRepository, Mockito.never())
                .findCurrentByBookerId(anyLong(), any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getAllByOwnerIdAndFindStatus_shouldReturnBookings_whenFindStatusIsWaiting() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatus(
                        user.getId(), BookingStatus.WAITING, Sort.by("start").descending()))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Mockito.when(bookingRepository.findByItemOwnerId(user.getId())).thenReturn(List.of(booking));

        // Act
        Collection<BookingResponseDto> result =
                bookingService.getAllByOwnerIdAndFindStatus(user.getId(), BookingFindStatus.WAITING);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStatus(
                        user.getId(),
                        BookingStatus.WAITING,
                        Sort.by("start").descending());
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnBookings_whenStatusIsFuture() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Mockito.when(bookingRepository.findByBookerIdAndStartAfter(
                        eq(user.getId()),
                        any(LocalDateTime.class),
                        eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerId(eq(user.getId()))).thenReturn(List.of(booking));


        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.FUTURE);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartAfter(eq(user.getId()), any(LocalDateTime.class), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldThrowUserNotFoundException_whenRenterDoesNotExist() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.FUTURE));

        Mockito.verify(bookingRepository, Mockito.never()).findByBookerId(any(Long.class));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnEmptyList_whenRenterHasNoBookings() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerId(user.getId())).thenReturn(List.of(booking));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.FUTURE);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(bookingRepository, Mockito.times(1)).findByBookerId(user.getId());
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnBookings_whenStatusIsCurrent() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findCurrentByBookerId(eq(user.getId()), any(LocalDateTime.class), eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(user.getId()))).thenReturn(List.of(new Booking()));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.CURRENT);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findCurrentByBookerId(eq(user.getId()), any(LocalDateTime.class), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnBookings_whenStatusIsPast() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndEndBefore(eq(user.getId()), any(LocalDateTime.class), eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(user.getId()))).thenReturn(List.of(new Booking()));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.PAST);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndEndBefore(eq(user.getId()), any(LocalDateTime.class), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnBookings_whenStatusIsWaiting() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(user.getId()))).thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository.findByBookerIdAndStatus(eq(user.getId()), eq(BookingStatus.WAITING), eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.WAITING);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatus(eq(user.getId()), eq(BookingStatus.WAITING), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnBookings_whenStatusIsRejected() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdAndStatus(eq(user.getId()), eq(BookingStatus.REJECTED), eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(user.getId()))).thenReturn(List.of(new Booking()));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.REJECTED);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatus(eq(user.getId()), eq(BookingStatus.REJECTED), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldReturnBookings_whenStatusIsAll() {
        // Arrange
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerId(eq(user.getId()), eq(Sort.by("start").descending())))
                .thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(user.getId()))).thenReturn(List.of(new Booking()));
        Mockito.when(bookingMapper.toResponseList(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));

        // Act
        Collection<BookingResponseDto> result = bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.ALL);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerId(eq(user.getId()), eq(Sort.by("start").descending()));
    }

    @Test
    void getAllByRenterIdAndFindStatus_shouldThrowUnauthorizedUserGetBookingsException_whenUserIsUnauthorized() {
        Mockito.when(bookingRepository.findByBookerId(Mockito.eq(user.getId()))).thenReturn(List.of());
        Mockito.when(userRepository.existsById(Mockito.eq(user.getId()))).thenReturn(true);

        Assertions.assertThrows(UnauthorizedUserGetBookingsException.class,
                () -> bookingService.getAllByRenterIdAndFindStatus(user.getId(), BookingFindStatus.CURRENT));

        Mockito.verify(bookingRepository, Mockito.never()).findCurrentByBookerId(anyLong(), any(LocalDateTime.class), any(Sort.class));
    }
}

