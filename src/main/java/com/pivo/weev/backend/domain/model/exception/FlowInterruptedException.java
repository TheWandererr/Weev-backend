package com.pivo.weev.backend.domain.model.exception;

import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.REASON;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FlowInterruptedException extends RuntimeException {

    private final String code;
    private final String reason;
    private final HttpStatus httpStatus;

    public FlowInterruptedException(String code) {
        this(code, null, BAD_REQUEST);
    }

    public FlowInterruptedException(String code, String reason) {
        this(code, reason, BAD_REQUEST);
    }

    public FlowInterruptedException(String code, String reason, HttpStatus httpStatus) {
        this.code = code;
        this.reason = reason;
        this.httpStatus = httpStatus;
    }

    public Map<String, Object> buildDetails() {
        Map<String, Object> details = new HashMap<>();
        if (isNotBlank(reason)) {
            details.put(REASON, reason);
        }
        return details;
    }
}
