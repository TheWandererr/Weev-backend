package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.user.ProfileRest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponse {

    private final ProfileRest profile;
}
