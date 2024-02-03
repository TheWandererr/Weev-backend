package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.meet.ImageRest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageResponse {

    private ImageRest photo;
}
