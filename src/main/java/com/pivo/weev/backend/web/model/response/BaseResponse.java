package com.pivo.weev.backend.web.model.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pivo.weev.backend.web.model.error.Error;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonInclude(NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

  private Error error;
  private ResponseMessage message;
  private Map<String, Object> details;

  public BaseResponse(ResponseMessage message) {
    this.message = message;
  }

  protected BaseResponse(BaseResponse copy) {
    this.message = copy.message;
    this.error = copy.error;
    this.details = copy.details;
  }

  public BaseResponse(Error error, ResponseMessage message) {
    this(error, message, null);
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  public ResponseMessage getMessage() {
    return message;
  }

  public void setMessage(ResponseMessage message) {
    this.message = message;
  }

  public Map<String, Object> getDetails() {
    if (isNull(details)) {
      details = new HashMap<>();
    }
    return details;
  }

  public void setDetails(Map<String, Object> details) {
    this.details = details;
  }

  public enum ResponseMessage {
    SUCCESS,
    CREATED,
    UNAUTHORIZED,
    ERROR,
    FORBIDDEN,
    TOO_MANY_REQUESTS
  }

  @JsonIgnore
  public boolean isFailed() {
    return nonNull(error);
  }

  @JsonIgnore
  public boolean isUnauthorized() {
    return ResponseMessage.UNAUTHORIZED == message;
  }

  @JsonIgnore
  public boolean isForbidden() {
    return ResponseMessage.FORBIDDEN == message;
  }

  @JsonIgnore
  public boolean isTooManyRequests() {
    return ResponseMessage.TOO_MANY_REQUESTS == message;
  }
}
