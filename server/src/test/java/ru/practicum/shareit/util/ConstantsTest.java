package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantsTest {
    @Test
    void shouldHaveCorrectUserIdHeader() {
        // Arrange & Act
        String actualHeader = Constants.USER_ID_HEADER;

        // Assert
        assertEquals("X-Sharer-User-Id", actualHeader, "The USER_ID_HEADER constant should be 'X-Sharer-User-Id'");
    }
}
