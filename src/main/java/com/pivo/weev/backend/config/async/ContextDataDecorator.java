package com.pivo.weev.backend.config.async;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static com.pivo.weev.backend.utils.LocaleUtils.setRequestLocale;
import static org.slf4j.MDC.clear;
import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;
import static org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes;
import static org.springframework.web.context.request.RequestContextHolder.setRequestAttributes;

import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

@Component
@RequiredArgsConstructor
public class ContextDataDecorator implements TaskDecorator {

    private final ApplicationContext applicationContext;

    @Override
    public Runnable decorate(Runnable runnable) {
        Locale mainThreadLocale = getAcceptedLocale();
        JwtHolder mainTreadJwtHolder = getJwtHolder();
        RequestAttributes mainThreadRequestContext = currentRequestAttributes();
        return () -> {
            try {
                JwtHolder threadJwtHolder = getJwtHolder();
                copyProperties(mainTreadJwtHolder, threadJwtHolder);
                setRequestAttributes(mainThreadRequestContext);
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
