package com.pivo.weev.backend.domain.service.image;

import static com.pivo.weev.backend.domain.utils.Constants.CompressingParams.MAX_SCALING;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.FILE_COMPRESSING_ERROR;
import static com.pivo.weev.backend.utils.IOUtils.getFormat;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.file.UploadableImage;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageCompressingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCompressingService.class);

    private final ApplicationLoggingHelper loggingHelper;
    private final ConfigService configsWrapper;

    public UploadableImage compress(MultipartFile photo) {
        try {
            BufferedImage compressed = compress(photo.getInputStream(), getScale(photo));
            return new UploadableImage(photo.getOriginalFilename(), getFormat(photo), compressed);
        } catch (IOException exception) {
            String reason = ofNullable(exception.getCause())
                    .map(Throwable::getMessage)
                    .orElse(null);
            LOGGER.error(loggingHelper.buildLoggingError(exception, null));
            throw new FlowInterruptedException(FILE_COMPRESSING_ERROR, reason, NOT_ACCEPTABLE);
        }
    }

    private BufferedImage compress(InputStream inputStream, double scale) throws IOException {
        return Thumbnails.of(inputStream)
                         .scale(scale)
                         .outputQuality(1)
                         .asBufferedImage();
    }

    private Double getScale(MultipartFile file) {
        Map<Long, Double> config = configsWrapper.getImageCompressingScale();
        long size = file.getSize();
        return config.entrySet().stream()
                     .filter(entry -> size <= entry.getKey())
                     .map(Entry::getValue)
                     .findFirst()
                     .orElse(MAX_SCALING);
    }

}
