package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ActiveProfiles("server")
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestSimpleDto simpleDto;
    private ItemRequestWithResponsesDto detailedDto;

    @BeforeEach
    void setUp() {
        simpleDto = new ItemRequestSimpleDto(1L, "Test request", LocalDateTime.now());
        detailedDto = new ItemRequestWithResponsesDto(1L, "Detailed request", LocalDateTime.now(), List.of());
    }

    @Test
    void createItemRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestPostDto postDto = new ItemRequestPostDto("New request description");
        Mockito.when(itemRequestService.createRequest(any(ItemRequestPostDto.class), anyLong()))
                .thenReturn(simpleDto);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(simpleDto.getId()))
                .andExpect(jsonPath("$.description").value(simpleDto.getDescription()));
    }

    @Test
    void getUserRequestsWithResponses_shouldReturnListOfRequests() throws Exception {
        Mockito.when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(detailedDto));

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(detailedDto.getId()))
                .andExpect(jsonPath("$[0].description").value(detailedDto.getDescription()));
    }

    @Test
    void getAllRequests_shouldReturnListOfAllRequests() throws Exception {
        Mockito.when(itemRequestService.getAllRequests())
                .thenReturn(List.of(simpleDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(simpleDto.getId()))
                .andExpect(jsonPath("$[0].description").value(simpleDto.getDescription()));
    }

    @Test
    void getSpecificItemRequest_shouldReturnRequestWithResponses() throws Exception {
        Mockito.when(itemRequestService.getByIdAndRequesterId(anyLong(), anyLong()))
                .thenReturn(detailedDto);

        mockMvc.perform(get("/requests/{itemRequestId}", 1L)
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(detailedDto.getId()))
                .andExpect(jsonPath("$.description").value(detailedDto.getDescription()));
    }
}
