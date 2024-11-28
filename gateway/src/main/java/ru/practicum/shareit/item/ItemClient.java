package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemPostDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> update(long ownerId, long itemId, ItemPatchDto itemUpdateData) {
        return patch("" + itemId, ownerId, itemUpdateData);
    }

    public ResponseEntity<Object> getByOwnerId(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getByOwnerIdAndSearchText(long userId, String text) {
        String path = "/search?text={searchString}";
        Map<String, Object> parameters = Map.of(
                "searchString", text
        );
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getByIdAndOwnerId(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }
}
