package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("""
            SELECT ir
            FROM ItemRequest ir
            LEFT JOIN FETCH ir.items
            WHERE ir.requester.id = :requesterId
            ORDER BY ir.created DESC
            """)
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(@Param("requesterId") long requesterId);

    List<ItemRequest> findAllByOrderByCreatedDesc();

    @EntityGraph(attributePaths = "items")
    @Query("""
            SELECT ir
            FROM ItemRequest ir
            WHERE ir.id = :requestId
            ORDER BY ir.created DESC
            """)
    Optional<ItemRequest> findByIdWithItems(@Param("requestId") long requestId);
}