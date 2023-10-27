package com.pivo.weev.backend.rest.model.error;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class PopupRest {

    private final String titleCode;
    private final String messageCode;
    private List<ButtonRest> buttons;

    @Getter
    @AllArgsConstructor
    public static class ButtonRest {

        private final String code;
    }

}
