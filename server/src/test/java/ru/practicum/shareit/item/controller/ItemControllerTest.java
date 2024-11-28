package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDetailedResponseDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constants;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ItemController.class)
@ActiveProfiles("server")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final String userIdHeader = Constants.USER_ID_HEADER;

    @Test
    void createItemByUser_whenRequestIdNull_shouldCallCreateItemByUser() throws Exception {
        // Arrange
        long userId = 1L;
        ItemPostDto itemPostDto = new ItemPostDto("Item name", "Description", true, null);
        ItemResponseDto responseDto = new ItemResponseDto(1L, "Item name", "Description", true);

        Mockito.when(itemService.createItemByUser(userId, itemPostDto))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(responseDto.getAvailable()));

        Mockito.verify(itemService).createItemByUser(userId, itemPostDto);
    }

    @Test
    void createItemByUser_whenRequestIdPresent_shouldCallCreateItemOnRequest() throws Exception {
        // Arrange
        long userId = 1L;
        long requestId = 10L;
        ItemPostDto itemPostDto = new ItemPostDto("Item name", "Description", true, requestId);
        ItemResponseDto responseDto = new ItemResponseDto(1L, "Item name", "Description", true);

        Mockito.when(itemService.createItemOnRequest(userId, requestId, itemPostDto))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(responseDto.getAvailable()));

        Mockito.verify(itemService).createItemOnRequest(userId, requestId, itemPostDto);
    }

    @Test
    void updateOwnerItem_shouldCallUpdateUserItem() throws Exception {
        // Arrange
        long ownerId = 1L;
        long itemId = 100L;
        ItemPatchDto patchDto = new ItemPatchDto("Updated name", "Updated description", false);
        ItemResponseDto responseDto = new ItemResponseDto(itemId, "Updated name", "Updated description", false);

        Mockito.when(itemService.updateItemByIdAndOwnerId(ownerId, itemId, patchDto))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(userIdHeader, ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(responseDto.getAvailable()));

        Mockito.verify(itemService).updateItemByIdAndOwnerId(ownerId, itemId, patchDto);
    }

    @Test
    void getAllOwnerItemsWithComments_shouldCallGetOwnerItemsWithComments() throws Exception {
        // Arrange
        long ownerId = 1L;
        List<ItemDetailedResponseDto> items = List.of(
                new ItemDetailedResponseDto(1L, "Item 1", "Desc 1", true, null, null, null),
                new ItemDetailedResponseDto(2L, "Item 2", "Desc 2", false, null, null, null)
        );

        Mockito.when(itemService.getOwnerItemsWithComments(ownerId))
                .thenReturn(items);

        // Act & Assert
        mockMvc.perform(get("/items")
                        .header(userIdHeader, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()))
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()));

        Mockito.verify(itemService).getOwnerItemsWithComments(ownerId);
    }

    @Test
    void getItemOfOwnerById_shouldCallGetItemWithCommentsById() throws Exception {
        // Arrange
        long ownerId = 1L;
        long itemId = 100L;
        ItemDetailedResponseDto responseDto = new ItemDetailedResponseDto(itemId, "Item name", "Desc", true, null, null, null);

        Mockito.when(itemService.getItemWithCommentsById(itemId, ownerId))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(userIdHeader, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.description").value(responseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(responseDto.getAvailable()));

        Mockito.verify(itemService).getItemWithCommentsById(itemId, ownerId);
    }

    // Utility method to convert objects to JSON
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}