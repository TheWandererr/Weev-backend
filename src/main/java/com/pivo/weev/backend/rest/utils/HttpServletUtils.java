package com.pivo.weev.backend.rest.utils;

import static com.pivo.weev.backend.common.utils.ArrayUtils.findFirst;
import static com.pivo.weev.backend.common.utils.ArrayUtils.first;
import static com.pivo.weev.backend.common.utils.StreamUtils.map;
import static com.pivo.weev.backend.common.utils.StreamUtils.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.common.utils.ArrayUtils;
import com.pivo.weev.backend.common.utils.CollectionUtils;
import com.pivo.weev.backend.domain.utils.Constants.Errors;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.utils.Constants.Cookies;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class HttpServletUtils {

  private static final Set<String> HIDDEN_HEADERS_NAMES = Set.of(COOKIE.toLowerCase());
  private static final Set<String> IP_ADDRESS_HEADERS = Set.of(
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
  );

  public static HttpServletRequest getCurrentRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
  }

  public static Map<String, String> getRequestParams() {
    return getRequestParams(getCurrentRequest());
  }

  public static Map<String, String> getRequestParams(HttpServletRequest request) {
    return stream(request.getParameterMap().entrySet()).collect(toMap(Entry::getKey, entry -> first(entry.getValue()).orElse(EMPTY)));
  }

  public static Optional<String> getHeader(String name) {
    return getHeader(getCurrentRequest(), name);
  }

  public static Optional<String> getHeader(HttpServletRequest request, String name) {
    return ofNullable(request.getHeader(name)).filter(StringUtils::isNotBlank);
  }

  private static String getHeader(HttpServletRequest request, String name, String defaultValue) {
    return getHeader(request, name).orElseGet(() -> defaultValue);
  }

  public static Optional<String> getCookie(String name) {
    return getCookie(getCurrentRequest(), name);
  }

  public static Optional<String> getCookie(HttpServletRequest request, String name) {
    return findFirst(request.getCookies(), cookie -> name.equals(cookie.getName())).map(Cookie::getValue);
  }

  public static Map<String, String> getCookies() {
    return getCookies(getCurrentRequest());
  }

  public static Optional<String> getDeviceId() {
    return getDeviceId(getCurrentRequest());
  }

  public static Optional<String> getDeviceId(HttpServletRequest request) {
    return getCookie(request, Cookies.DEVICE_ID);
  }

  public static Map<String, String> getCookies(HttpServletRequest request) {
    List<Cookie> cookies = ArrayUtils.toList(request.getCookies());
    return CollectionUtils.collect(cookies, toMap(Cookie::getName, Cookie::getValue));
  }

  public static String getIpAddressValue() {
    final HttpServletRequest currentRequest = getCurrentRequest();
    return getIpAddressValue(currentRequest);
  }

  public static String getIpAddressValue(HttpServletRequest request) {
    return ofNullable(request.getRemoteAddr())
        .orElseGet(() -> map(IP_ADDRESS_HEADERS, request::getHeader).filter(Objects::nonNull).findFirst().orElse(null));
  }

  public static Map<String, String> collectHeaders() {
    HttpServletRequest request = getCurrentRequest();
    return collectHeaders(request);
  }

  public static Map<String, String> collectHeaders(HttpServletRequest request) {
    return CollectionUtils.collect(Collections.list(request.getHeaderNames()), toMap(name -> name, request::getHeader));
  }

  public static Map<String, String> collectPublicHeaders() {
    Map<String, String> publicHeaders = collectHeaders();
    HIDDEN_HEADERS_NAMES.forEach(publicHeaders::remove);
    return publicHeaders;
  }

  public static Map<String, String> collectPublicHeaders(HttpServletRequest request) {
    Map<String, String> publicHeaders = collectHeaders(request);
    HIDDEN_HEADERS_NAMES.forEach(publicHeaders::remove);
    return publicHeaders;
  }

  public static void writeResponse(BaseResponse source, HttpServletResponse destination, HttpStatus status, ObjectMapper writer)
      throws IOException {
    destination.setContentType(APPLICATION_JSON_VALUE);
    destination.setStatus(status.value());
    try (ServletOutputStream outputStream = destination.getOutputStream()) {
      writer.writeValue(outputStream, source);
    }
  }

  public static String getAuthorizationValue() {
    return getAuthorizationValue(getCurrentRequest());
  }

  public static String getAuthorizationValue(HttpServletRequest request) {
    return getHeader(request, AUTHORIZATION).or(() -> getCookie(request, AUTHORIZATION))
                                            .map(HttpServletUtils::parseAuthorizationToken)
                                            .filter(StringUtils::isNotBlank)
                                            .orElseThrow(
                                                () -> new AuthorizationServiceException(Errors.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR));
  }

  private static String parseAuthorizationToken(String authorizationValue) {
    if (isBlank(authorizationValue)) {
      return null;
    }
    return authorizationValue.startsWith(BEARER.getValue())
        ? authorizationValue.substring(BEARER.getValue().length())
        : authorizationValue;
  }

}
