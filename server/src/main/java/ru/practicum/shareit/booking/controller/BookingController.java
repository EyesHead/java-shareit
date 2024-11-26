package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingFindStatus;
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
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto, ownerId);
        log.info("[SERVER | CONTROLLER] Booking successfully created and saved in db. ID = {}", response.getId());
        log.debug("[SERVER | CONTROLLER] Booking with id '{}' was received with data: {}", response.getId(), response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable(value = "bookingId") long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
            @RequestParam("approved") boolean isApproved) {
        log.info("[SERVER | CONTROLLER] Received request to approve booking by bookingId='{}' with approval status='{}' and ownerId='{}'",
                bookingId, isApproved, ownerId);
        BookingResponseDto response = bookingService.approveBooking(bookingId, ownerId, isApproved);
        log.info("[SERVER | CONTROLLER] Booking approval processed for bookingId='{}' with approval status='{}'", bookingId, isApproved);
        log.debug("[SERVER | CONTROLLER] Booking approval response for bookingId='{}': {}", bookingId, response);
        return response;
    }

    @GetMapping
    public Collection<BookingResponseDto> getAllBookingsOfRenter(
            @RequestHeader(Constants.USER_ID_HEADER) long renterId,
            @RequestParam(defaultValue = "ALL") String status) {
        log.info("[SERVER | CONTROLLER] Request to get bookings for renterId='{}' with bookingStatus='{}'", renterId, status);
        Collection<BookingResponseDto> bookings = bookingService.getAllByRenterIdAndFindStatus(renterId, BookingFindStatus.valueOf(status));
        log.info("[SERVER | CONTROLLER] Fetched {} bookings for renterId='{}' with status='{}'", bookings.size(), renterId, status);
        log.debug("[SERVER | CONTROLLER] Bookings for renterId='{}' with status='{}': {}", renterId, status, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getAllBookingsOfItemOwner(
            @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") String status) {
        log.info("[SERVER | CONTROLLER] Request to get bookings for ownerId='{}' with bookingStatus='{}'", ownerId, status);
        Collection<BookingResponseDto> bookings = bookingService.getAllByOwnerIdAndFindStatus(ownerId, BookingFindStatus.valueOf(status));
        log.info("[SERVER | CONTROLLER] Fetched {} bookings for ownerId='{}' with status='{}'", bookings.size(), ownerId, status);
        log.debug("[SERVER | CONTROLLER] Bookings for ownerId='{}' with status='{}': {}", ownerId, status, bookings);
        return bookings;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] Request to get booking by bookingId='{}' and userId='{}'", bookingId, userId);
        BookingResponseDto booking = bookingService.getBookingByIdAndUserId(bookingId, userId);
        log.info("[SERVER | CONTROLLER] Fetched booking with bookingId='{}' for userId='{}'", bookingId, userId);
        log.debug("[SERVER | CONTROLLER] Booking details for bookingId='{}': {}", bookingId, booking);
        return booking;
    }
}