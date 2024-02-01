package com.pivo.weev.backend.rest.model.error;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class PopupRest {

    private final String title;
    private final String message;
    private List<ButtonRest> buttons;
    private List<String> details;

    public List<String> getDetails() {
        if (isNull(details)) {
            details = new ArrayList<>();
        }
        return details;
    }

    public List<ButtonRest> getButtons() {
        if (isNull(buttons)) {
            buttons = new ArrayList<>();
        }
        return buttons;
    }

    public record ButtonRest(String code) {

    }

}
