package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.SUCCESS;

import com.pivo.weev.backend.domain.service.ModerationService;
import com.pivo.weev.backend.rest.model.request.EventDeclineRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.DeclinationReasonsResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/moderation")
@RequiredArgsConstructor
@Validated
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/reasons")
    @PreAuthorize("hasAnyAuthority('SCOPE_moderation')")
    public DeclinationReasonsResponse getDeclinationReasons() {
        List<String> reasons = moderationService.getDeclinationReasons();
        return new DeclinationReasonsResponse(reasons);
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_moderation')")
    public BaseResponse confirmEvent(@PathVariable Long id) {
        moderationService.confirmEvent(id);
        return new BaseResponse(SUCCESS);
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_moderation')")
    public BaseResponse declineEvent(@PathVariable Long id, @Valid @RequestBody EventDeclineRequest request) {
        moderationService.declineEvent(id, request.getDeclinationReason());
        return new BaseResponse(SUCCESS);
    }
}
