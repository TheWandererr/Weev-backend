package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.PopupButtons.OK;
import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.FLOW_INTERRUPTED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PERMISSIONS_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.UNAUTHORIZED_ERROR;

import com.pivo.weev.backend.rest.model.error.PopupRest;
import com.pivo.weev.backend.rest.model.error.PopupRest.ButtonRest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PopupRestFactory {

    private final ButtonRestFactory buttonRestFactory;

    public PopupRest create(String code, String message, List<String> buttonCodes) {
        List<ButtonRest> buttons = mapToList(buttonCodes, buttonRestFactory::create);
        return new PopupRest(code, message, buttons, null);
    }

    public PopupRest forbidden() {
        ButtonRest button = buttonRestFactory.ok();
        return new PopupRest(PERMISSIONS_ERROR + TITLE, PERMISSIONS_ERROR, List.of(button), null);
    }

    public PopupRest unauthorized() {
        ButtonRest button = buttonRestFactory.login();
        return new PopupRest(UNAUTHORIZED_ERROR + TITLE, UNAUTHORIZED_ERROR, List.of(button), null);
    }

    public PopupRest unauthorized(String reason) {
        ButtonRest button = buttonRestFactory.login();
        return new PopupRest(UNAUTHORIZED_ERROR + TITLE, UNAUTHORIZED_ERROR, List.of(button), List.of(reason));
    }

    public PopupRest somethingWentWrong() {
        return create(FLOW_INTERRUPTED_ERROR + TITLE, FLOW_INTERRUPTED_ERROR, List.of(OK));
    }

    public PopupRest resourceNotFound(String detail) {
        PopupRest popup = somethingWentWrong();
        popup.getDetails().add(detail);
        return popup;
    }
}
