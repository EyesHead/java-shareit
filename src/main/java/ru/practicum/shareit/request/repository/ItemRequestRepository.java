package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.entity.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

}
