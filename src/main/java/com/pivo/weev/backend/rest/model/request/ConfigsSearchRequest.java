package com.pivo.weev.backend.rest.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigsSearchRequest {

    private String keyRegex = "\\w+";
}
