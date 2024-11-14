package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING,  // Ожидание подтверждения
    APPROVED, // Подтверждено владельцем
    REJECTED, // Отклонено владельцем
    CANCELED  // Отменено создателем
}
