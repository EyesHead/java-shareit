package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.config.Constants;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                            @RequestBody @Valid BookingPostDto bookingPostDto) {
        log.info("Received request to create booking by user ID {}", ownerId);
        BookingResponseDto response = bookingService.createBooking(bookingPostDto, ownerId);
        log.info("Booking successfully created for user ID {}", ownerId);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable(value = "bookingId") long bookingId,
                                             @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                             @RequestParam("approved") boolean isApproved) {
        log.info("Received request to approve booking with ID = '{}' by owner with ID = '{}'", bookingId, ownerId);
        BookingResponseDto response = bookingService.approveBooking(bookingId, ownerId, isApproved);
        log.info("Booking ID {} approved status set to {}", bookingId, isApproved);
        return response;
    }

    @GetMapping
    public Collection<BookingResponseDto> getAllBookingsByRenterAndStatus(@RequestHeader(Constants.USER_ID_HEADER) long bookerId,
                                                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("Received request to retrieve all bookings for renter ID {} with state {}", bookerId, state);
        Collection<BookingResponseDto> bookings = bookingService.getAllBookingsByBookerIdAndBookingStatus(bookerId, state);
        log.info("Retrieved {} bookings for renter ID {}", bookings.size(), bookerId);
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getBookingsByOwnerAndStatus(@RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                                                      @RequestParam(defaultValue = "ALL") String state) {
        log.info("Received request to retrieve all bookings for owner ID {} with state {}", ownerId, state);
        Collection<BookingResponseDto> bookings = bookingService.getByOwnerIdAndBookingStatus(ownerId, state);
        log.info("Retrieved {} bookings for owner ID {}", bookings.size(), ownerId);
        return bookings;

    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable long bookingId,
                                             @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("Received request to retrieve booking with ID {} by user ID {}", bookingId, userId);
        BookingResponseDto response = bookingService.getBookingByIdAndAuthorizedUserId(bookingId, userId);
        log.info("Retrieved booking with ID {} for user ID {}", bookingId, userId);
        return response;
    }
}
