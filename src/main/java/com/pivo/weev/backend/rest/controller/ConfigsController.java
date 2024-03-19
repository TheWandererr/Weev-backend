package com.pivo.weev.backend.rest.controller;

import com.google.firebase.remoteconfig.Parameter;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.rest.model.request.ConfigsSearchRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
public class ConfigsController {

    private final ConfigService configService;

    @PostMapping
    public Map<String, Parameter> searchParameters(@RequestBody ConfigsSearchRequest request) {
        return configService.getParameters(request.getKeyRegex());
    }
}
