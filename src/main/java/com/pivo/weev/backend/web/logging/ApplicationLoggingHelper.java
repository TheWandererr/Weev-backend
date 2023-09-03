package com.pivo.weev.backend.web.logging;

import static com.pivo.weev.backend.web.utils.HttpServletUtils.getCurrentRequest;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.web.model.common.LogMessage;
import com.pivo.weev.backend.web.model.response.BaseResponse;
import com.pivo.weev.backend.web.utils.Constants.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationLoggingHelper {

  private final ObjectMapper restResponseMapper;

  public String buildLoggingError(Exception exception, String failure) {
    LogMessage message = buildLogMessage(exception, failure, true);
    return toJson(message);
  }

  public String buildLoggingError(Exception exception, String failure, boolean includeBody) {
    LogMessage message = buildLogMessage(exception, failure, includeBody);
    return toJson(message);
  }

  public String buildLoggingError(BaseResponse baseResponse, String failure) {
    return buildLoggingError(baseResponse, failure, true);
  }

  public String buildLoggingError(BaseResponse baseResponse, String failure, boolean includeBody) {
    return buildLoggingError(getCurrentRequest(), baseResponse, failure, includeBody);
  }

  public String buildLoggingError(HttpServletRequest request, BaseResponse baseResponse, String failure) {
    LogMessage message = buildLogMessage(request, baseResponse, failure, true);
    return toJson(message);
  }

  public String buildLoggingError(HttpServletRequest request, BaseResponse baseResponse, String failure, boolean includeBody) {
    LogMessage message = buildLogMessage(request, baseResponse, failure, includeBody);
    return toJson(message);
  }

  public String buildLoggingInfo(HttpServletRequest request, BaseResponse baseResponse, String failure) {
    LogMessage message = buildLogMessage(request, baseResponse, failure, false);
    return toJson(message);
  }

  private LogMessage buildLogMessage(HttpServletRequest request, BaseResponse baseResponse, String failure, boolean includeBody) {
    LogMessage message = LogMessage.fromRequest(request, includeBody);
    message.getDetails().putAll(baseResponse.getDetails());
    ofNullable(baseResponse.getError()).ifPresent(error -> message.getDetails().put(error.getErrorCode(), error.getMessage()));
    message.setFailure(failure);
    return message;
  }

  private LogMessage buildLogMessage(Exception exception, String failure, boolean includeBody) {
    HttpServletRequest request = getCurrentRequest();
    LogMessage message = LogMessage.fromRequest(request, includeBody);
    message.setDetails(Map.of(ResponseDetails.REASON, exception.getMessage()));
    message.setFailure(failure);
    return message;
  }

  private String toJson(LogMessage logMessage) {
    try {
      return restResponseMapper.writeValueAsString(logMessage);
    } catch (Exception e) {
      return logMessage.toString();
    }
  }
}
