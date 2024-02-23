package com.pivo.weev.backend.rest.model.user;

import com.pivo.weev.backend.domain.model.user.MeetsStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRest extends UserSnapshotRest {

    private String name;
    private MeetsStatistics meetsStatistics;
}
