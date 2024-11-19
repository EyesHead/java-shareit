package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.error.InvalidBookingDateException;
import ru.practicum.shareit.error.InvalidBookingStatusException;
import ru.practicum.shareit.util.Constants;

import java.time.LocalDateTime;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@Profile("gateway")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsByRenter(@RequestHeader(Constants.USER_ID_HEADER) long renterId,
                                                      @RequestParam(name = "state", defaultValue = "all") String statusRequest) {
        BookingStatus bookingStatus = BookingStatus.get(statusRequest)
                .orElseThrow(() -> new InvalidBookingStatusException("Unknown bookingStatus: " + statusRequest));
        log.info("[GATEWAY] Get bookings with bookingStatus='{}' and renter with id='{}'", bookingStatus, renterId);
        return bookingClient.getBookingsByRenterId(renterId, bookingStatus);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfItemOwner(
            @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") String statusRequest) {

        BookingStatus bookingStatus = BookingStatus.get(statusRequest)
                .orElseThrow(() -> new InvalidBookingStatusException("Unknown bookingStatus: " + statusRequest));

        log.info("[GATEWAY] Get bookings with bookingStatus='{}' and item owner with id='{}'", bookingStatus, ownerId);
        return bookingClient.getBookingsByOwnerId(ownerId, bookingStatus);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                 @PathVariable long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable(value = "bookingId") long bookingId,
                                                 @RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                                 @RequestParam("approved") boolean isApproved) {
        log.info("[GATEWAY] Approve booking with bookingId = '{}' by owner with ID = '{}'. IsApproved = '{}'", bookingId, ownerId, isApproved);
        return bookingClient.approveBooking(bookingId, ownerId, isApproved);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                           @RequestBody @Valid BookingRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();

        if (start.isEqual(end) || start.isAfter(end)) {
            throw new InvalidBookingDateException(start, end);
        }
        return bookingClient.bookItem(userId, requestDto);
    }
}
