package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBookingRequestDtoSerialization() throws Exception {
        // Создаем пример DTO
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        // Сериализуем объект в JSON
        String json = objectMapper.writeValueAsString(bookingRequestDto);

        // Проверяем, что JSON содержит все ожидаемые поля
        assertThat(json).contains("itemId");
        assertThat(json).contains("start");
        assertThat(json).contains("end");
    }

    @Test
    void testBookingRequestDtoDeserialization() throws Exception {
        // Создаем строку JSON для десериализации
        String json = "{\"itemId\":1,\"start\":\"2024-11-26T10:00:00\",\"end\":\"2024-11-27T10:00:00\"}";

        // Десериализуем JSON в объект BookingRequestDto
        BookingRequestDto bookingRequestDto = objectMapper.readValue(json, BookingRequestDto.class);

        // Проверяем, что объект десериализовался правильно
        assertThat(bookingRequestDto.getItemId()).isEqualTo(1L);
        assertThat(bookingRequestDto.getStart()).isEqualTo(LocalDateTime.of(2024, 11, 26, 10, 0));
        assertThat(bookingRequestDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 11, 27, 10, 0));
    }
}
