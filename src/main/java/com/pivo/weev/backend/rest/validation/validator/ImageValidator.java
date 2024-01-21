package com.pivo.weev.backend.rest.validation.validator;

import static com.pivo.weev.backend.rest.utils.Constants.FileMediaTypes.IMAGE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.validation.annotation.ValidImage;
import com.pivo.weev.backend.utils.IOUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageValidator.class);

    private final ApplicationLoggingHelper loggingHelper;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (isNull(file)) {
            return true;
        }
        try {
            String mediaType = IOUtils.getMediaType(file);
            if (isBlank(mediaType) || !mediaType.startsWith(IMAGE)) {
                return false;
            }
        } catch (IOException exception) {
            LOGGER.error(loggingHelper.buildLoggingError(exception, null));
            return false;
        }
        return true;
    }
}
