package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.FLOW_INTERRUPTED_ERROR;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.FORBIDDEN;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.UNAUTHORIZED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.LOGIN_REQUIRED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.NOT_ENOUGH_PERMISSIONS;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.SOMETHING_WENT_WRONG;
import static com.pivo.weev.backend.rest.utils.Constants.PopupButtons.OK;

import com.pivo.weev.backend.rest.model.error.PopupRest;
import com.pivo.weev.backend.rest.model.error.PopupRest.ButtonRest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PopupRestFactory {

    private final ButtonRestFactory buttonRestFactory;

    public PopupRest create(String errorCode, String messageCode, List<String> buttonCodes) {
        List<ButtonRest> buttons = mapToList(buttonCodes, buttonRestFactory::create);
        return new PopupRest(errorCode, messageCode, buttons);
    }

    public PopupRest forbidden() {
        ButtonRest button = buttonRestFactory.ok();
        return new PopupRest(FORBIDDEN, NOT_ENOUGH_PERMISSIONS, List.of(button));
    }

    public PopupRest unauthorized() {
        ButtonRest button = buttonRestFactory.login();
        return new PopupRest(UNAUTHORIZED, LOGIN_REQUIRED, List.of(button));
    }

    public PopupRest somethingWentWrong() {
        return create(FLOW_INTERRUPTED_ERROR, SOMETHING_WENT_WRONG, List.of(OK));
    }
}
