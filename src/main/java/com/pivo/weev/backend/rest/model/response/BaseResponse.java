package com.pivo.weev.backend.rest.model.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.SUCCESS;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pivo.weev.backend.rest.model.error.AlertRest;
import com.pivo.weev.backend.rest.model.error.NotificationRest;
import com.pivo.weev.backend.rest.model.error.PopupRest;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(NON_EMPTY)
@Getter
@Setter
public class BaseResponse {

    private PopupRest popup;
    private AlertRest alert;
    private NotificationRest notification;
    private ResponseMessage message;
    private Map<String, Object> details;

    public BaseResponse() {
        this(SUCCESS);
    }

    public BaseResponse(PopupRest popup, ResponseMessage responseMessage) {
        this(popup, responseMessage, null);
    }

    public BaseResponse(ResponseMessage responseMessage) {
        this(null, responseMessage, null);
    }

    public BaseResponse(AlertRest alert, ResponseMessage message) {
        this.alert = alert;
        this.message = message;
    }

    public BaseResponse(PopupRest popup, ResponseMessage message, Map<String, Object> details) {
        this.popup = popup;
        this.message = message;
        this.details = details;
    }

    public BaseResponse(NotificationRest notification, ResponseMessage message) {
        this.notification = notification;
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        if (isNull(details)) {
            details = new HashMap<>();
        }
        return details;
    }

    public enum ResponseMessage {
        SUCCESS,
        CREATED,
        UNAUTHORIZED,
        ERROR,
        FORBIDDEN,
        TOO_MANY_REQUESTS
    }

    @JsonIgnore
    public boolean isFailed() {
        return nonNull(popup);
    }

    @JsonIgnore
    public boolean isUnauthorized() {
        return ResponseMessage.UNAUTHORIZED == message;
    }

    @JsonIgnore
    public boolean isForbidden() {
        return ResponseMessage.FORBIDDEN == message;
    }

    @JsonIgnore
    public boolean isTooManyRequests() {
        return ResponseMessage.TOO_MANY_REQUESTS == message;
    }
}
