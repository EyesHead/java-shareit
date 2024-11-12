package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(long itemId, long ownerId);

    Collection<Item> findAllByOwnerId(long userId);

    @Query("""
        SELECT it
        FROM Item it
        WHERE (lower(it.description) LIKE lower(CONCAT('%', :searchText, '%'))
        OR lower(it.name) LIKE lower(CONCAT('%', :searchText, '%')) )
        """)
    Collection<Item> findBySearchText(@Param("searchText") String searchText);

    @Query("""
        SELECT i FROM Item i
        LEFT JOIN FETCH i.comments c
        WHERE i.owner.id = :ownerId
    """)
    Collection<Item> findAllByOwnerIdWithComments(@Param("ownerId") long ownerId);
}