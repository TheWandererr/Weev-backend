package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MESSAGES_PER_PAGE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.domain.service.messaging.ChatService;
import com.pivo.weev.backend.rest.annotation.ResourceOwner;
import com.pivo.weev.backend.rest.mapping.rest.ChatRestMapper;
import com.pivo.weev.backend.rest.model.messaging.ChatRest;
import com.pivo.weev.backend.rest.model.response.ChatsResponse;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class ChatsController {

    private final ChatService chatService;

    @ResourceOwner
    @GetMapping("/{userId}/chats")
    public ChatsResponse getChats(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long userId, @RequestParam(required = false, defaultValue = MESSAGES_PER_PAGE) @Min(1) Integer historySize) {
        List<Chat> chats = chatService.getChats(userId, historySize);
        List<ChatRest> restChats = getMapper(ChatRestMapper.class).map(chats);
        return new ChatsResponse(restChats);
    }
}
