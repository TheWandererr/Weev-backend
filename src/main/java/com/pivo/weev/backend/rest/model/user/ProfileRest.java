package com.pivo.weev.backend.rest.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRest extends UserSnapshotRest {

    private String name;
    private int participatedMeets;
    private int createdMeets;
}
