package com.pivo.weev.backend.rest.model.common;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.collectHeaders;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getRequestParams;
import static java.util.Objects.isNull;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogMessage {

  private Map<String, String> headers;
  private Map<String, Object> details;
  private Map<String, String> body;
  private String method;
  private String failure;
  private String uri;

  public Map<String, String> getHeaders() {
    if (isNull(headers)) {
      headers = new HashMap<>();
    }
    return headers;
  }

  public Map<String, Object> getDetails() {
    if (isNull(details)) {
      details = new HashMap<>();
    }
    return details;
  }

  public Map<String, String> getBody() {
    if (isNull(body)) {
      body = new HashMap<>();
    }
    return body;
  }

  public static LogMessage fromRequest(HttpServletRequest request, boolean includeBody) {
    LogMessage message = new LogMessage();
    message.setHeaders(collectHeaders(request));
    if (includeBody) {
      message.setBody(getRequestParams(request));
    }
    message.setMethod(request.getMethod());
    message.setUri(request.getRequestURI());
    return message;
  }

  @Override
  public String toString() {
    return "ERROR : {" +
        "headers : " + headers +
        ", details :" + details +
        ", body :" + body +
        ", method : " + method +
        ", failure :" + failure +
        ", uri : " + uri + "}";
  }
}
