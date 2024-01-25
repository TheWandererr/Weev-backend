package com.pivo.weev.backend.rest.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameAvailabilityResponse extends BaseResponse {

    private final boolean available;
}
