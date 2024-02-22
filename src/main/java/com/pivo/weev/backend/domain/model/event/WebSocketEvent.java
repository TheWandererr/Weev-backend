package com.pivo.weev.backend.domain.model.event;

import static com.pivo.weev.backend.domain.utils.Constants.MessagingPayload.CHAT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.pivo.weev.backend.domain.model.messaging.payload.ChatSnapshotPayload;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    public static class WebSocketMessageModel {

        private final EventType eventType;
        private String recipient;
        private String destination;
        private Map<String, ? extends Serializable> payload;

        public boolean hasRecipient() {
            return isNotBlank(recipient);
        }

        public boolean hasDestination() {
            return isNotBlank(destination);
        }

        public boolean hasChat() {
            return getPayload().containsKey(CHAT);
        }

        public ChatSnapshotPayload getChat() {
            return (ChatSnapshotPayload) getPayload().get(CHAT);
        }
    }

    public enum EventType {

        CHAT_CREATED
    }
}
