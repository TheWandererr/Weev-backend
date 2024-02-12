package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.websocket.model.ChatWs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeetConfirmationResponse {

    private ChatWs chat;
}
