package com.pivo.weev.backend.domain.model.exception;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ReasonableException extends RuntimeException {

    private final String errorCode;
    private final String reason;
    private final HttpStatus httpStatus;

    public ReasonableException(String errorCode) {
        this(errorCode, null, BAD_REQUEST);
    }

    public ReasonableException(String errorCode, String reason) {
        this(errorCode, reason, BAD_REQUEST);
    }

    public Map<String, Object> buildDetails() {
        Map<String, Object> details = new HashMap<>();
        if (isNotBlank(reason)) {
            details.put("reason", reason);
        }
        return details;
    }
}
