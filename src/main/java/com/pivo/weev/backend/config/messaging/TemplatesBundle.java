package com.pivo.weev.backend.config.messaging;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.TEMPLATE_PROCESSING_ERROR;
import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemplatesBundle {

    private final Configuration freeMarkerConfiguration;

    public String getTemplateContent(String name, Object source) {
        try {
            Template template = getTemplate(name);
            return processTemplateIntoString(template, source);
        } catch (IOException | TemplateException exception) {
            throw new FlowInterruptedException(TEMPLATE_PROCESSING_ERROR, exception.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    public Template getTemplate(String name) throws IOException {
        return freeMarkerConfiguration.getTemplate(name, getAcceptedLocale());
    }
}
