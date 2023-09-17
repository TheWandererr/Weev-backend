package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.SUCCESS;

import com.pivo.weev.backend.domain.service.ModerationService;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @PutMapping("/events/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_moderation')")
    public BaseResponse confirmEvent(@PathVariable Long id) {
        moderationService.confirmEvent(id);
        return new BaseResponse(SUCCESS);
    }
}
