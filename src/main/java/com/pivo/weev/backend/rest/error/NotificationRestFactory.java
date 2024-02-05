package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.CREDENTIALS_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.UNAUTHORIZED_ERROR;

import com.pivo.weev.backend.rest.model.error.NotificationRest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRestFactory {

    public NotificationRest badCredentials() {
        return new NotificationRest(CREDENTIALS_ERROR + TITLE);
    }

    public NotificationRest unauthorized() {
        return new NotificationRest(UNAUTHORIZED_ERROR + TITLE);
    }

    public NotificationRest forbidden(String cause) {
        return new NotificationRest(cause + TITLE);
    }
}
