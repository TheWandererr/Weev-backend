package com.pivo.weev.backend.rest.interceptor;

import com.pivo.weev.backend.domain.model.common.ThreadContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class ThreadContextInterceptor implements HandlerInterceptor {

    private final List<? extends ThreadContext> contextBeans;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        contextBeans.forEach(ThreadContext::clear);
    }
}
