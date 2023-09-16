package com.pivo.weev.backend.domain.model.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationResult {

    private boolean valid;
    private String errorMessageCode;

    public static ValidationResult failed(String errorMessageCode) {
        return new ValidationResult(false, errorMessageCode);
    }

}
