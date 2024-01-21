package com.pivo.weev.backend.domain.model.file;

import java.awt.image.BufferedImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Image {

    private final String name;
    private final String format;
    private final BufferedImage source;
}
