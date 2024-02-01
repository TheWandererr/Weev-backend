package com.pivo.weev.backend.config.async;

import static com.pivo.weev.backend.utils.LocaleUtils.setRequestLocale;
import static org.slf4j.MDC.clear;

import com.pivo.weev.backend.utils.LocaleUtils;
import java.util.Locale;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

@Component
public class ContextDataDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Locale locale = LocaleUtils.getAcceptedLocale();
        return () -> {
            try {
                setRequestLocale(locale, true);
                runnable.run();
            } finally {
                clearData();
            }
        };
    }

    private void clearData() {
        clear();

    }
}
