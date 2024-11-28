package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingFindStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ActiveProfiles("server")
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_HEADER = Constants.USER_ID_HEADER;

    @Test
    void testBookItem() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING,
                new UserResponseDto(1L, "User", "user@example.com"),
                new ItemResponseDto(1L, "Item", "Description", true)
        );

        Mockito.when(bookingService.createBooking(any(BookingRequestDto.class), eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().toString()));
    }

    @Test
    void testApproveBooking() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED,
                new UserResponseDto(1L, "User", "user@example.com"),
                new ItemResponseDto(1L, "Item", "Description", true)
        );

        Mockito.when(bookingService.approveBooking(1L, 1L, true))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().toString()));
    }

    @Test
    void testGetAllBookingsOfRenter() throws Exception {
        List<BookingResponseDto> responseDtos = List.of(new BookingResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED,
                new UserResponseDto(1L, "User", "user@example.com"),
                new ItemResponseDto(1L, "Item", "Description", true)
        ));

        Mockito.when(bookingService.getAllByRenterIdAndFindStatus(1L, BookingFindStatus.ALL))
                .thenReturn(responseDtos);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("status", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtos.getFirst().getId()))
                .andExpect(jsonPath("$[0].status").value(responseDtos.getFirst().getStatus().toString()));
    }

    @Test
    void testGetAllBookingsOfItemOwner() throws Exception {
        List<BookingResponseDto> responseDtos = List.of(new BookingResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED,
                new UserResponseDto(1L, "User", "user@example.com"),
                new ItemResponseDto(1L, "Item", "Description", true)
        ));

        Mockito.when(bookingService.getAllByOwnerIdAndFindStatus(1L, BookingFindStatus.ALL))
                .thenReturn(responseDtos);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("status", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtos.getFirst().getId()))
                .andExpect(jsonPath("$[0].status").value(responseDtos.getFirst().getStatus().toString()));
    }

    @Test
    void testGetBooking() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED,
                new UserResponseDto(1L, "User", "user@example.com"),
                new ItemResponseDto(1L, "Item", "Description", true)
        );

        Mockito.when(bookingService.getBookingByIdAndUserId(1L, 1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().toString()));
    }
}
