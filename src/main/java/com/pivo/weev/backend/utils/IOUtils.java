package com.pivo.weev.backend.utils;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static javax.imageio.ImageIO.write;

import com.pivo.weev.backend.utils.Constants.Symbols;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
@Slf4j
public class IOUtils {

    private static final Tika TIKA = new Tika();

    public static Path getPath(URL url) throws URISyntaxException {
        return Paths.get(url.toURI());
    }

    public static String readInputStream(InputStream inputStream, Charset charset) throws IOException {
        return new String(inputStream.readAllBytes(), charset);
    }

    public static String readInputStream(InputStream inputStream) throws TikaException, IOException {
        return TIKA.parseToString(inputStream);
    }

    public static InputStream getInputStream(String string) {
        return new ByteArrayInputStream(getBytes(string));
    }

    public static byte[] getBytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] getBytes(BufferedImage bufferedImage, String format) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            write(bufferedImage, format, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            return new byte[0];
        }
    }

    public static boolean isNotEmpty(MultipartFile file) {
        return ofNullable(file).filter(not(MultipartFile::isEmpty)).isPresent();
    }

    public static boolean isEmpty(MultipartFile file) {
        return !isNotEmpty(file);
    }

    public static String getMediaType(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        return TIKA.detect(fileBytes);
    }

    public static String getFormat(MultipartFile file) throws IOException {
        String mediaType = getMediaType(file);
        return ArrayUtils.last(mediaType.split(Symbols.SLASH)).orElse(null);
    }
}
