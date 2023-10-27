package com.pivo.weev.backend.domain.model.exception;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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

    public ReasonableException(String errorCode, String reason, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.reason = reason;
        this.httpStatus = httpStatus;
    }

    public Map<String, Object> buildDetails() {
        Map<String, Object> details = new HashMap<>();
        if (isNotBlank(reason)) {
            details.put("reason", reason);
        }
        return details;
    }
}
