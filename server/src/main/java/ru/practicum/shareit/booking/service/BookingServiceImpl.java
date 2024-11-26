package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingFindStatus;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    BookingMapper bookingMapper;
    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingPostDto, long bookerId) {
        log.debug("[SERVER | SERVICE] Booking create by userId='{}', data:{}", bookerId, bookingPostDto);

        long itemId = bookingPostDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(bookerId));

        if (!item.getAvailable()) {
            throw new UnavailableItemForBookingException(itemId);
        }

        Booking bookingToSave = bookingMapper.toBooking(bookingPostDto);
        bookingToSave.setBooker(booker);
        bookingToSave.setItem(item);
        bookingToSave.setStatus(BookingStatus.WAITING);
        log.debug("[SERVER | SERVICE] Booking is valid and prepared for save: {}", bookingToSave);

        return bookingMapper.toResponse(bookingRepository.save(bookingToSave));
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(long bookingId, long ownerId, boolean isApproved) {
        log.debug("[SERVER | SERVICE] Approve booking with ID {} by owner ID {}", bookingId, ownerId);
        // Поиск бронирования
        Booking foundBooking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new UnauthorizedUserApproveBookingException(bookingId, ownerId));

        // Проверка текущего статуса бронирования
        BookingStatus currentBookingStatus = foundBooking.getStatus();
        if (currentBookingStatus != BookingStatus.WAITING) {
            throw new InvalidBookingStatusForApprovingException(currentBookingStatus);
        }

        // Обновление статуса в зависимости от ответа владельца
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        foundBooking.setStatus(newStatus);

        log.debug("Booking approval status updated to: {}", newStatus);
        return bookingMapper.toResponse(bookingRepository.save(foundBooking));
    }

    @Override
    @Transactional
    public Collection<BookingResponseDto> getAllByRenterIdAndFindStatus(long bookerId, BookingFindStatus status) {
        log.debug("Getting bookings by bookerId='{}' with status='{}'", bookerId, status);

        if (!userRepository.existsById(bookerId)) {
            throw new UserNotFoundException(bookerId);
        }

        if (bookingRepository.findByBookerId(bookerId).isEmpty()) {
            throw new UnauthorizedUserGetBookingsException(bookerId);
        }

        List<Booking> bookings = switch (status) {
            case CURRENT -> bookingRepository
                    .findCurrentByBookerId(bookerId, LocalDateTime.now(), Sort.by("start").descending());
            case PAST -> bookingRepository
                    .findByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), Sort.by("start").descending());
            case FUTURE -> bookingRepository
                    .findByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), Sort.by("start").descending());
            case WAITING -> bookingRepository
                    .findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, Sort.by("start").descending());
            case REJECTED -> bookingRepository
                    .findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, Sort.by("start").descending());
            default -> bookingRepository
                    .findByBookerId(bookerId, Sort.by("start").descending());
        };

        log.debug("Retrieved {} bookings for renter ID {}", bookings.size(), bookerId);
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional
    @Override
    public Collection<BookingResponseDto> getAllByOwnerIdAndFindStatus(long ownerId, BookingFindStatus status) {
        log.debug("Fetching bookings for item owner ID {} with status {}", ownerId, status);

        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        if (bookingRepository.findByItemOwnerId(ownerId).isEmpty()) {
            throw new UnauthorizedUserGetBookingsException(ownerId);
        }

        List<Booking> bookings = switch (status) {
            case CURRENT -> bookingRepository.findCurrentByItemOwnerId(
                    ownerId, LocalDateTime.now(), Sort.by("start").descending());
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBefore(
                    ownerId, LocalDateTime.now(), Sort.by("start").descending());
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfter(
                    ownerId, LocalDateTime.now(), Sort.by("start").descending());
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(
                    ownerId, BookingStatus.WAITING, Sort.by("start").descending());
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(
                    ownerId, BookingStatus.REJECTED, Sort.by("start").descending());
            case ALL -> bookingRepository.findAllByItemOwnerId(ownerId, Sort.by("start").descending());
        };
        log.debug("Retrieved {} bookings for item owner ID {}", bookings.size(), ownerId);
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    @Transactional
    public BookingResponseDto getBookingByIdAndUserId(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        // Если проверка пройдена, возвращаем данные о бронировании
        log.debug("Successfully retrieved booking by booking ID {} AND user ID {}", bookingId, userId);
        return bookingMapper.toResponse(booking);
    }
}