package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.PopupButtons.LOGIN;
import static com.pivo.weev.backend.rest.utils.Constants.PopupButtons.OK;

import com.pivo.weev.backend.rest.model.error.PopupRest.ButtonRest;
import org.springframework.stereotype.Component;

@Component
public class ButtonRestFactory {

    public ButtonRest ok() {
        return new ButtonRest(OK);
    }

    public ButtonRest login() {
        return new ButtonRest(LOGIN);
    }

    public ButtonRest create(String code) {
        return new ButtonRest(code);
    }
}
