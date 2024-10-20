package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item addItemToUser(long userId, Item item);

    boolean isItemOfUserExist(long itemId, long userId);

    Item updateUserItem(long userId, long itemId, Item itemUpdate);

    Optional<Item> findUserItemByItemId(long userId, long itemId);

    Collection<Item> findUserItems(long userId);

    Collection<Item> findUserItemBySearchText(long userId, String searchText);
}