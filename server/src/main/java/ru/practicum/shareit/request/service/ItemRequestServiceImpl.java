package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ItemRequestNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestMapper mapper;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;

    @Override
    public ItemRequestSimpleDto createRequest(ItemRequestPostDto itemRequestPostDto, long userId) {
        log.debug("[SERVER | SERVICE] createRequest called with userId: {}", userId);

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ItemRequest itemRequest = mapper.toEntity(itemRequestPostDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(requester);

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        log.debug("[SERVER | SERVICE] Created ItemRequest with id: {}. Request before mapping: {}",
                saved.getId(), saved);

        return mapper.toSimpleResponse(saved);
    }

    @Override
    public Collection<ItemRequestWithResponsesDto> getUserRequests(long userId) {
        log.debug("[SERVER | SERVICE] getUserRequests called with userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        log.debug("[SERVER | SERVICE] Found {} requests for userId: {}", requests.size(), userId);
        log.debug("[SERVER | SERVICE] Requests for user before mapping: {}", requests);

        return mapper.toDetailedResponseList(requests);
    }

    @Override
    public ItemRequestWithResponsesDto getByIdAndRequesterId(long requestId, long userId) {
        log.debug("[SERVER | SERVICE] getByIdAndRequesterId called with requestId: {}, userId: {}", requestId, userId);

        ItemRequest request = itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(requestId));

        log.debug("[SERVER | SERVICE] Returning ItemRequest with id: {}", request.getId());
        log.debug("[SERVER | SERVICE] Retrieved request before mapping: {}", request);
        return mapper.toDetailedResponse(request);
    }

    @Override
    public Collection<ItemRequestSimpleDto> getAllRequests() {
        log.debug("[SERVER | SERVICE] getAllOtherRequests called");

        Collection<ItemRequest> requests = itemRequestRepository.findAllByOrderByCreatedDesc();
        log.debug("[SERVER | SERVICE] Found {} requests created", requests.size());

        return mapper.toSimpleResponseList(requests);
    }
}