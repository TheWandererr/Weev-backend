package com.pivo.weev.backend.domain.model.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudResource {

    private String blobId;
    private String url;
    private String format;
    private Long authorId;
}
