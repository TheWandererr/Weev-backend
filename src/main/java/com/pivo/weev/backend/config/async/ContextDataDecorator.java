package com.pivo.weev.backend.config.async;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static com.pivo.weev.backend.utils.LocaleUtils.setRequestLocale;
import static org.slf4j.MDC.clear;

import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContextDataDecorator implements TaskDecorator {

    private final ApplicationContext applicationContext;

    @Override
    public Runnable decorate(Runnable runnable) {
        Locale mainThreadLocale = getAcceptedLocale();
        JwtHolder mainTreadJwtHolder = getJwtHolder();
        return () -> {
            try {
                JwtHolder threadJwtHolder = getJwtHolder();
                threadJwtHolder.setToken(mainTreadJwtHolder.getToken());
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
    }
}
