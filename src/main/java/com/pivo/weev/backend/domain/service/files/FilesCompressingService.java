package com.pivo.weev.backend.domain.service.files;

import static com.pivo.weev.backend.common.utils.IOUtils.getFormat;
import static com.pivo.weev.backend.domain.utils.Constants.CompressingParams.MAX_SCALING;
import static com.pivo.weev.backend.domain.utils.Constants.CompressingParams.SCALE_MAPPING;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.FILE_COMPRESSING_ERROR;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.model.file.Image;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FilesCompressingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesCompressingService.class);

    private final ApplicationLoggingHelper loggingHelper;

    public Image compress(MultipartFile file) {
        try {
            BufferedImage compressed = compress(file.getInputStream(), getScale(file));
            return new Image(getFormat(file), compressed);
        } catch (IOException exception) {
            String reason = ofNullable(exception.getCause())
                    .map(Throwable::getMessage)
                    .orElse(null);
            LOGGER.error(loggingHelper.buildLoggingError(exception, null));
            throw new ReasonableException(FILE_COMPRESSING_ERROR, reason, NOT_ACCEPTABLE);
        }
    }

    private BufferedImage compress(InputStream inputStream, double scale) throws IOException {
        return Thumbnails.of(inputStream)
                         .scale(scale)
                         .outputQuality(1)
                         .asBufferedImage();
    }

    private Double getScale(MultipartFile file) {
        long size = file.getSize();
        for (Entry<Long, Double> scaling : SCALE_MAPPING.entrySet()) {
            if (size <= scaling.getKey()) {
                return scaling.getValue();
            }
        }
        return MAX_SCALING;
    }

}
