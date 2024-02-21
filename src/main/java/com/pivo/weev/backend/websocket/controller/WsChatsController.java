package com.pivo.weev.backend.websocket.controller;


import static com.pivo.weev.backend.websocket.utils.Constants.UserDestinations.CHAT_PATTERN;
import static com.pivo.weev.backend.websocket.utils.StompUtils.getNickname;
import static java.lang.String.format;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.ChatMessageMapper;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.domain.service.messaging.ChatService;
import com.pivo.weev.backend.websocket.mapping.ws.MessageWsMapper;
import com.pivo.weev.backend.websocket.model.UserMessageWs;
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

    @MessageMapping("/chats.{chatId}.message")
    public void onSendMessage(@DestinationVariable String chatId, @Payload UserMessageWs message, StompHeaderAccessor accessor) {
        String nickname = getNickname(accessor);
        UserMessage domainMessage = (UserMessage) getMapper(ChatMessageMapper.class).map(message);
        UserMessage outputMessage = chatService.pushMessage(chatId, nickname, domainMessage);
        template.convertAndSend(format(CHAT_PATTERN, chatId), getMapper(MessageWsMapper.class).map(outputMessage));
    }
}
