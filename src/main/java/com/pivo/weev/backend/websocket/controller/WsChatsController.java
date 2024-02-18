package com.pivo.weev.backend.websocket.controller;


import static com.pivo.weev.backend.websocket.utils.StompUtils.getNickname;
import static java.lang.String.format;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.CommonMessageMapper;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.service.messaging.ChatService;
import com.pivo.weev.backend.websocket.mapping.ws.CommonMessageWsMapper;
import com.pivo.weev.backend.websocket.model.ChatMessageWs;
import com.pivo.weev.backend.websocket.utils.Constants.UserDestinations;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WsChatsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/chats.{chatId}")
    public void onSendMessage(@DestinationVariable Long chatId, @Payload ChatMessageWs message, StompHeaderAccessor accessor) {
        String nickname = getNickname(accessor);
        ChatMessage domainMessage = (ChatMessage) getMapper(CommonMessageMapper.class).map(message);
        ChatMessage outputMessage = chatService.pushMessage(chatId, nickname, domainMessage);
        template.convertAndSend(format(UserDestinations.CHAT_PATTERN, chatId), getMapper(CommonMessageWsMapper.class).map(outputMessage));
    }
}
