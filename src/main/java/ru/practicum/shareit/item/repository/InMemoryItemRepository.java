package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataIntegrityException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private final HashMap<Long, List<Item>> userItemsMap = new HashMap<>();
    private long id;

    @Override
    public Item addItemToUser(long userId, Item item) {
        log.info("Adding item for user with ID {}", userId);

        item.setId(generatePrimaryKey());
        item.setOwner(userId);

        userItemsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        log.debug("Item added: {}", item);

        return item;
    }

    @Override
    public boolean isItemOfUserExist(long itemId, long userId) {
        log.info("Checking if item with ID {} exists for user with ID {}", itemId, userId);

        boolean exists = userItemsMap.getOrDefault(userId, Collections.emptyList())
                .stream()
                .anyMatch(item -> item.getId().equals(itemId));

        log.debug("Item existence for user ID {}: {}", userId, exists);
        return exists;
    }

    @Override
    public Item updateUserItem(long userId, long itemId, Item itemUpdate) {
        log.info("Updating item with ID {} for user with ID {}", itemId, userId);

        List<Item> userItems = userItemsMap.get(userId);

        Item itemForPrepare = userItems
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny()
                .orElseThrow(() -> {
                    log.warn("Item with ID {} not found for update", itemId);
                    return new DataIntegrityException("Item with id = '" + itemId + "' should exist");
                });

        Item preparedItem = itemForPrepare.toBuilder()
                .name(itemUpdate.getName() != null ? itemUpdate.getName() : itemForPrepare.getName())
                .description(itemUpdate.getDescription() != null ? itemUpdate.getDescription() : itemForPrepare.getDescription())
                .available(itemUpdate.getAvailable() != null ? itemUpdate.getAvailable() : itemForPrepare.getAvailable())
                .build();

        userItems.removeIf(item -> item.getId().equals(itemUpdate.getId()));
        userItems.add(preparedItem);

        log.debug("Item updated: {}", preparedItem);
        return preparedItem;
    }

    @Override
    public Optional<Item> findUserItemByItemId(long userId, long itemId) {
        log.info("Finding item with ID {} for user with ID {}", itemId, userId);

        Optional<Item> foundItem = userItemsMap.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny();

        if (foundItem.isPresent()) {
            log.debug("Item found: {}", foundItem.get());
        } else {
            log.warn("Item with ID {} for user with ID {} not found", itemId, userId);
        }

        return foundItem;
    }

    @Override
    public Collection<Item> findUserItems(long userId) {
        log.info("Retrieving items for user with ID {}", userId);

        Collection<Item> items = userItemsMap.getOrDefault(userId, Collections.emptyList());
        log.debug("Items retrieved for user with ID {}: {}", userId, items);

        return items;
    }

    @Override
    public Collection<Item> findUserItemBySearchText(long userId, String searchText) {
        log.info("Searching items for user with ID {} by search text: {}", userId, searchText);

        final String searchTextLowerCase = searchText.toLowerCase();
        Collection<Item> foundItems = userItemsMap.getOrDefault(userId, Collections.emptyList())
                .stream()
                .filter(item -> {
                    String itemName = item.getName().toLowerCase();
                    String itemDescription = item.getDescription().toLowerCase();
                    Boolean available = item.getAvailable();
                    return available && (itemName.contains(searchTextLowerCase) || itemDescription.contains(searchTextLowerCase));
                })
                .toList();

        log.debug("Found items by search text for user with ID {}: {}", userId, foundItems);
        return foundItems;
    }

    private long generatePrimaryKey() {
        id++;
        log.debug("Generated new item ID: {}", id);
        return id;
    }
}