package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.BAD_CREDENTIALS;

import com.pivo.weev.backend.rest.model.error.NotificationRest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRestFactory {

    public NotificationRest badCredentials() {
        return new NotificationRest(BAD_CREDENTIALS);
    }
}
