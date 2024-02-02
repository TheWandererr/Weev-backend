package com.pivo.weev.backend.rest.model.meet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetSnapshotRest {

    private Long id;
    private String header;
    private ImageRest photo;
}
