package com.pivo.weev.backend.websocket.controller;


import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.messaging.ChatMessage;
import com.pivo.weev.backend.domain.service.websocket.ChatService;
import com.pivo.weev.backend.utils.Constants.WebSocketParams.MessageCodes;
import com.pivo.weev.backend.websocket.mapping.ws.MessageWsMapper;
import com.pivo.weev.backend.websocket.model.ChatWs;
import com.pivo.weev.backend.websocket.model.MessageWs;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats")
    public void onChatCreated(@Payload ChatWs chat) {
        ChatMessage systemMessage = chatService.newSystemMessage(chat.getId(), MessageCodes.MEET_CHAT_CREATED);
        MessageWs message = getMapper(MessageWsMapper.class).map(systemMessage);
        String destination = "/topic/chats." + chat.getId() + ".messages";
        messagingTemplate.convertAndSend(destination, message);
    }
}
