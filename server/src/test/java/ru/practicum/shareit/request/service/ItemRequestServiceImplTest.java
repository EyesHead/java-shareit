package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestMapper mapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private ItemRequest request;
    private ItemRequestPostDto postDto;
    private ItemRequestWithResponsesDto responseDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        request = new ItemRequest(1L, "Request description", LocalDateTime.now(), user, Collections.emptyList());
        postDto = new ItemRequestPostDto("Request description");
        responseDto = new ItemRequestWithResponsesDto(1L, "Request description", request.getCreated(), Collections.emptyList());
    }

    @Test
    void createRequest_shouldCreateSuccessfully() {
        ItemRequestSimpleDto itemRequestSimpleDto = new ItemRequestSimpleDto(responseDto.getId(), responseDto.getDescription(), responseDto.getCreated());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toEntity(postDto)).thenReturn(request);
        when(itemRequestRepository.save(request)).thenReturn(request);
        when(mapper.toSimpleResponse(request)).thenReturn(itemRequestSimpleDto);

        ItemRequestSimpleDto result = service.createRequest(postDto, user.getId());

        assertThat(result).isEqualTo(itemRequestSimpleDto);
        verify(userRepository).findById(user.getId());
        verify(itemRequestRepository).save(request);
    }

    @Test
    void createRequest_shouldThrowUserNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createRequest(postDto, user.getId()));

        verify(userRepository).findById(user.getId());
        verifyNoInteractions(itemRequestRepository, mapper);
    }

    @Test
    void getUserRequests_shouldReturnRequests() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId()))
                .thenReturn(Collections.singletonList(request));
        when(mapper.toDetailedResponseList(Collections.singletonList(request)))
                .thenReturn(Collections.singletonList(responseDto));

        var result = service.getUserRequests(user.getId());

        assertThat(result).containsExactly(responseDto);
        verify(itemRequestRepository).findAllByRequesterIdOrderByCreatedDesc(user.getId());
    }

    @Test
    void getUserRequests_shouldThrowUserNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.getUserRequests(user.getId()));

        verify(userRepository).existsById(user.getId());
        verifyNoInteractions(itemRequestRepository, mapper);
    }

    @Test
    void getByIdAndRequesterId_shouldReturnRequest() {
        when(itemRequestRepository.findByIdWithItems(request.getId())).thenReturn(Optional.of(request));
        when(mapper.toDetailedResponse(request)).thenReturn(responseDto);

        var result = service.getByIdAndRequesterId(request.getId(), user.getId());

        assertThat(result).isEqualTo(responseDto);
        verify(itemRequestRepository).findByIdWithItems(request.getId());
    }

    @Test
    void getByIdAndRequesterId_shouldThrowItemRequestNotFoundException() {
        when(itemRequestRepository.findByIdWithItems(request.getId())).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> service.getByIdAndRequesterId(request.getId(), user.getId()));

        verify(itemRequestRepository).findByIdWithItems(request.getId());
        verifyNoInteractions(mapper);
    }
}