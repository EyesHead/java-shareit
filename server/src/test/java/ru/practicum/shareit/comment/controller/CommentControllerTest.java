package ru.practicum.shareit.comment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.util.Constants;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("server")
@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    void createCommentByRenter_ShouldReturnCreatedComment_WhenValidInput() throws Exception {
        // Arrange
        long itemId = 1L;
        long authorId = 123L;
        CommentPostDto commentPostDto = new CommentPostDto("Great item!");
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                42L, "Great item!", "John Doe", LocalDateTime.now()
        );

        when(commentService.createComment(any(CommentPostDto.class), eq(authorId), eq(itemId)))
                .thenReturn(commentResponseDto);

        // Act & Assert
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Constants.USER_ID_HEADER, authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Great item!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponseDto.getAuthorName()))
                .andExpect(jsonPath("$.created").exists());

        verify(commentService, times(1)).createComment(any(CommentPostDto.class), eq(authorId), eq(itemId));
    }

    @Test
    void createCommentByRenter_ShouldReturnBadRequest_WhenNoUserIdHeader() throws Exception {
        // Arrange
        long itemId = 1L;

        // Act & Assert
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Great item!\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }
}