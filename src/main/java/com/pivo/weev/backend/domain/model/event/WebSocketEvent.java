package com.pivo.weev.backend.domain.model.event;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class WebSocketEvent extends ApplicationEvent {

    public WebSocketEvent(Object source) {
        super(source);
        if (!(source instanceof WebSocketMessageModel)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public WebSocketMessageModel getSource() {
        return (WebSocketMessageModel) source;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebSocketMessageModel {

        private ChatSnapshot chatSnapshot;
        private String recipient;
        private EventType eventType;

        public boolean hasRecipient() {
            return isNotBlank(recipient);
        }
    }

    public enum EventType {

        CHAT_CREATED
    }
}
