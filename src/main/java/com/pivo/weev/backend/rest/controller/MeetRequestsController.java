package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.service.meet.MeetRequestsService;
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
public class MeetRequestsController {

    private final MeetRequestsService meetRequestsService;

    @PostMapping("/meets/{meetId}/joining/requests/{requestId}/confirmation")
    @ResponseStatus(value = CREATED)
    public BaseResponse confirmJoinRequest(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long meetId,
                                           @Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long requestId) {
        meetRequestsService.confirmJoinRequest(meetId, requestId);
        return new BaseResponse();
    }
}
