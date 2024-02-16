package com.pivo.weev.backend.config.async;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static com.pivo.weev.backend.utils.LocaleUtils.setRequestLocale;
import static org.slf4j.MDC.clear;
import static org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes;
import static org.springframework.web.context.request.RequestContextHolder.setRequestAttributes;

import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@RequiredArgsConstructor
public class ContextDataDecorator implements TaskDecorator {

    private final ApplicationContext applicationContext;

    @Override
    public Runnable decorate(Runnable runnable) {
        Locale mainThreadLocale = getAcceptedLocale();
        JwtHolder mainTreadJwtHolder = getJwtHolder();
        RequestAttributes mainThreadAttributes = RequestContextHolder.currentRequestAttributes();
        return () -> {
            try {
                JwtHolder threadJwtHolder = getJwtHolder();
                threadJwtHolder.setToken(mainTreadJwtHolder.getToken());
                setRequestAttributes(mainThreadAttributes);
                setRequestLocale(mainThreadLocale, true);
                runnable.run();
            } finally {
                clearData();
            }
        };
    }

    private JwtHolder getJwtHolder() {
        return applicationContext.getBean(JwtHolder.class);
    }

    private void clearData() {
        clear();
        getJwtHolder().clear();
        resetRequestAttributes();
    }
}
