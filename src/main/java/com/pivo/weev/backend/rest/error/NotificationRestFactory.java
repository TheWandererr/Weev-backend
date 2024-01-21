package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.BAD_CREDENTIALS;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.UNAUTHORIZED;

import com.pivo.weev.backend.rest.model.error.NotificationRest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRestFactory {

    public NotificationRest badCredentials() {
        return new NotificationRest(BAD_CREDENTIALS + TITLE);
    }

    public NotificationRest unauthorized() {
        return new NotificationRest(UNAUTHORIZED + TITLE);
    }
}
