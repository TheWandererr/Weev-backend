package com.pivo.weev.backend.rest.model.request;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatsSearchRequest {

    private Map<String, Long> chatsOrdinals;

    public Map<String, Long> getChatsOrdinals() {
        if (isNull(chatsOrdinals)) {
            chatsOrdinals = new HashMap<>();
        }
        return chatsOrdinals;
    }
}
