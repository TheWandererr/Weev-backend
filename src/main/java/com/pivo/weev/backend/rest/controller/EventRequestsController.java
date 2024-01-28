package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.service.event.EventRequestsService;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventRequestsController {

    private final EventRequestsService eventRequestsService;

    @PostMapping("/events/{eventId}/joining/requests/{requestId}/confirmation")
    @ResponseStatus(value = CREATED)
    public BaseResponse confirmJoinRequest(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long eventId,
                                           @Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long requestId) {
        eventRequestsService.confirmJoinRequest(eventId, requestId);
        return new BaseResponse();
    }
}
