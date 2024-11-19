package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.util.Constants;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@Profile("gateway")
public class CommentGatewayController {
    private final CommentClient commentClient;

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createCommentForItemByUser(@PathVariable("itemId") long itemId,
                                                             @RequestHeader(Constants.USER_ID_HEADER) long authorId,
                                                             @RequestBody CommentRequestDto commentRequest) {
        log.info("Get comment of itemId='{}', authorId='{}'", itemId, authorId);
        return commentClient.createCommentForItemByUser(itemId, authorId, commentRequest);
    }
}
