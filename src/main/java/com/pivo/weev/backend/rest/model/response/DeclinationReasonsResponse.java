package com.pivo.weev.backend.rest.model.response;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeclinationReasonsResponse extends BaseResponse {

    private List<String> declinationReasons;

    public List<String> getDeclinationReasons() {
        if (isNull(declinationReasons)) {
            declinationReasons = new ArrayList<>();
        }
        return declinationReasons;
    }
}
