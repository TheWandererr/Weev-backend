package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MESSAGES_PER_PAGE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.messaging.chat.CommonChatMessage;
import com.pivo.weev.backend.domain.service.messaging.ChatService;
import com.pivo.weev.backend.rest.mapping.rest.CommonChatMessageRestMapper;
import com.pivo.weev.backend.rest.model.messaging.CommonChatMessageRest;
import com.pivo.weev.backend.rest.model.response.ChatMessagesResponse;
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
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Validated
public class ChatsController {

    private final ChatService chatService;

    @GetMapping("/{id}/messages")
    public ChatMessagesResponse getChatMessages(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) Integer offset,
                                                @RequestParam(required = false, defaultValue = MESSAGES_PER_PAGE) @Min(1) Integer historySize) {
        List<CommonChatMessage> messages = chatService.getChatMessages(id, offset, historySize);
        List<CommonChatMessageRest> restMessages = getMapper(CommonChatMessageRestMapper.class).map(messages);
        return new ChatMessagesResponse(restMessages);
    }
}
