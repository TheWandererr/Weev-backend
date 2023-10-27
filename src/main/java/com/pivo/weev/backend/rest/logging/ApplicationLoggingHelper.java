package com.pivo.weev.backend.rest.logging;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getCurrentRequest;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.rest.model.common.LogMessageRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.utils.Constants.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationLoggingHelper {

    private final ObjectMapper restResponseMapper;

    public String buildLoggingError(Exception exception, String failure) {
        LogMessageRest message = buildLogMessage(exception, failure, true);
        return toJson(message);
    }

    public String buildLoggingError(Exception exception, String failure, boolean includeBody) {
        LogMessageRest message = buildLogMessage(exception, failure, includeBody);
        return toJson(message);
    }

    public String buildLoggingError(BaseResponse baseResponse, String failure) {
        return buildLoggingError(baseResponse, failure, true);
    }

    public String buildLoggingError(BaseResponse baseResponse, String failure, boolean includeBody) {
        return buildLoggingError(getCurrentRequest(), baseResponse, failure, includeBody);
    }

    public String buildLoggingError(HttpServletRequest request, BaseResponse baseResponse, String failure) {
        LogMessageRest message = buildLogMessage(request, baseResponse, failure, true);
        return toJson(message);
    }

    public String buildLoggingError(HttpServletRequest request, BaseResponse baseResponse, String failure, boolean includeBody) {
        LogMessageRest message = buildLogMessage(request, baseResponse, failure, includeBody);
        return toJson(message);
    }

    public String buildLoggingInfo(HttpServletRequest request, BaseResponse baseResponse, String failure) {
        LogMessageRest message = buildLogMessage(request, baseResponse, failure, false);
        return toJson(message);
    }

    private LogMessageRest buildLogMessage(HttpServletRequest request, BaseResponse baseResponse, String failure, boolean includeBody) {
        LogMessageRest message = LogMessageRest.fromRequest(request, includeBody);
        message.getDetails().putAll(baseResponse.getDetails());
        ofNullable(baseResponse.getPopup())
                .ifPresent(popup -> message.getDetails().put(popup.getTitleCode(), popup.getMessageCode()));
        message.setFailure(failure);
        return message;
    }

    private LogMessageRest buildLogMessage(Exception exception, String failure, boolean includeBody) {
        HttpServletRequest request = getCurrentRequest();
        LogMessageRest message = LogMessageRest.fromRequest(request, includeBody);
        message.setDetails(Map.of(ResponseDetails.REASON, exception.getMessage()));
        message.setFailure(failure);
        return message;
    }

    private String toJson(LogMessageRest logMessageRest) {
        try {
            return restResponseMapper.writeValueAsString(logMessageRest);
        } catch (Exception e) {
            return logMessageRest.toString();
        }
    }
}
