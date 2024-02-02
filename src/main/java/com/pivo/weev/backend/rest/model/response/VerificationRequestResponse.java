package com.pivo.weev.backend.rest.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerificationRequestResponse extends BaseResponse {

    private String method;
}
