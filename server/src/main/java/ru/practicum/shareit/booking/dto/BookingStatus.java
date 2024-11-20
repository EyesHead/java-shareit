package ru.practicum.shareit.booking.dto;

public enum BookingStatus {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING,
    // Подтвержденные
    APPROVED
}