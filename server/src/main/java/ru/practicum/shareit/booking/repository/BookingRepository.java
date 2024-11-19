package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.entity.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndItemOwnerId(long bookingId, long ownerId);

    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByBookerId(long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(long bookerId, LocalDateTime start, Sort sort);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :bookerId
        AND b.start <= :now
        AND b.end > :now
        """)
    List<Booking> findCurrentByBookerId(@Param("bookerId") long bookerId,
                                        @Param("now") LocalDateTime now,
                                        Sort sort);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
        AND b.start <= :now
        AND b.end > :now
        """)
    List<Booking> findCurrentByItemOwnerId(@Param("ownerId") long bookerId,
                                           @Param("now") LocalDateTime now,
                                           Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(long ownerId, LocalDateTime now, Sort start);

    List<Booking> findByItemOwnerIdAndStartAfter(long ownerId, LocalDateTime now, Sort start);

    List<Booking> findByItemOwnerIdAndStatus(long ownerId, BookingStatus bookingStatus, Sort start);

    List<Booking> findAllByItemOwnerId(long ownerId, Sort start);

    Optional<Booking> findByItemIdAndBookerId(long itemId, long renterId);

    Collection<Booking> findByItemOwnerId(long ownerId);
}
