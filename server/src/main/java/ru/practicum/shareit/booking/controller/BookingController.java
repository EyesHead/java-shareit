package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Constants;

import java.util.Collection;

@RestController
@Profile("server")
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto bookItem(
            @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
            @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("[SERVER | CONTROLLER] Received request to create booking by ownerId='{}'", ownerId);
        return bookingService.createBooking(bookingRequestDto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable(value = "bookingId") long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
            @RequestParam("approved") boolean isApproved) {
        log.info("[SERVER | CONTROLLER] Request to approve?='{}' booking by bookingId='{}' and ownerId='{}'",
                isApproved, bookingId, ownerId);
        return bookingService.approveBooking(bookingId, ownerId, isApproved);
    }

    @GetMapping
    public Collection<BookingResponseDto> getAllBookingsOfRenter(
            @RequestHeader(Constants.USER_ID_HEADER) long renterId,
            @RequestParam(defaultValue = "ALL") String status) {

        log.info("[SERVER | CONTROLLER] Request to get bookings by renterId='{}' and bookingStatus='{}'", renterId, status);
        return bookingService.getAllByRenterIdAndStatus(renterId, BookingStatus.valueOf(status));
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getAllBookingsOfItemOwner(
            @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") String status) {

        log.info("[SERVER | CONTROLLER] Request to get bookings by ownerId='{}' and bookingStatus='{}'", ownerId, status);
        return bookingService.getAllByItemOwnerIdAndStatus(ownerId, BookingStatus.valueOf(status));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] Request to get booking by bookingId='{}' and userId='{}'", bookingId, userId);
        return bookingService.getBookingByIdAndUserId(bookingId, userId);
    }
}
