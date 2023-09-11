package com.pivo.weev.backend.rest.model.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pivo.weev.backend.rest.model.error.Error;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseResponse {

  private Error error;
  private ResponseMessage message;
  private Map<String, Object> details;

  public BaseResponse(Error error, ResponseMessage responseMessage) {
    this(error, responseMessage, null);
  }

  public BaseResponse(ResponseMessage responseMessage) {
    this(null, responseMessage, null);
  }

  public Map<String, Object> getDetails() {
    if (isNull(details)) {
      details = new HashMap<>();
    }
    return details;
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
