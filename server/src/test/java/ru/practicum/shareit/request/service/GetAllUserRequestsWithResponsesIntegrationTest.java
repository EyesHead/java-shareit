package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.ResponseOnItemRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GetAllUserRequestsWithResponsesIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void getUserRequests_whenUserHasRequests_shouldReturnRequestsSortedByDate() {
        // Arrange
        UserResponseDto userResponse = createUser("user1@test.com");
        long userId = userResponse.getId();

        createRequest(userId, "First request");
        createRequest(userId, "Second request");
        createRequest(userId, "Third request");

        // Act
        Collection<ItemRequestWithResponsesDto> userRequests = itemRequestService.getUserRequests(userId);

        // Assert
        assertThat(userRequests).hasSize(3);

        List<ItemRequestWithResponsesDto> requestsList = new ArrayList<>(userRequests);
        assertThat(requestsList.get(0).getDescription()).isEqualTo("Third request");
        assertThat(requestsList.get(1).getDescription()).isEqualTo("Second request");
        assertThat(requestsList.get(2).getDescription()).isEqualTo("First request");
    }

    @Test
    void getUserRequests_whenUserHasNoRequests_shouldReturnEmptyCollection() {
        // Arrange
        UserResponseDto userResponse = createUser("user2@test.com");
        long userId = userResponse.getId();

        // Act
        Collection<ItemRequestWithResponsesDto> userRequests = itemRequestService.getUserRequests(userId);

        // Assert
        assertThat(userRequests).isEmpty();
    }

    @Test
    void getUserRequests_whenUserDoesNotExist_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> itemRequestService.getUserRequests(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUserRequests_withResponses_shouldIncludeResponsesInResult() {
        // Arrange
        UserResponseDto requester = createUser("user3@test.com");
        long requesterId = requester.getId();

        UserResponseDto owner = createUser("owner@test.com");
        long ownerId = owner.getId();

        ItemRequestPostDto requestDto = new ItemRequestPostDto("Request with item");
        ItemRequestSimpleDto request = itemRequestService.createRequest(requestDto, requesterId);

        ItemPostDto itemDto = new ItemPostDto("Item name", "Description", true, request.getId());
        itemService.createItemOnRequest(ownerId, request.getId(), itemDto);

        // Act
        Collection<ItemRequestWithResponsesDto> userRequests = itemRequestService.getUserRequests(requesterId);

        // Assert
        assertThat(userRequests).hasSize(1);
        ItemRequestWithResponsesDto retrievedRequest = userRequests.iterator().next();
        assertThat(retrievedRequest.getItems()).hasSize(1);

        ResponseOnItemRequestDto response = retrievedRequest.getItems().get(0);
        assertThat(response.getName()).isEqualTo("Item name");
        assertThat(response.getOwnerId()).isEqualTo(ownerId);
    }

    // Методы по созданию пользователей и запросов
    private UserResponseDto createUser(String email) {
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setEmail(email);
        userRequest.setName("Test User");
        return userService.createUser(userRequest);
    }

    private void createRequest(long userId, String description) {
        ItemRequestPostDto requestDto = new ItemRequestPostDto(description);
        itemRequestService.createRequest(requestDto, userId);
    }
}