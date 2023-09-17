package com.pivo.weev.backend.rest.controller;

import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.service.event.EventsOperatingService;
import com.pivo.weev.backend.rest.mapping.domain.CreatableEventMapper;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/events")
@RequiredArgsConstructor
@Validated
public class EventsController {

    private final EventsOperatingService eventsOperatingService;

    @PostMapping
    @ResponseStatus(value = CREATED)
    public BaseResponse createEvent(@Valid @ModelAttribute EventSaveRequest request) {
        CreatableEvent sample = getMapper(CreatableEventMapper.class).map(request);
        eventsOperatingService.saveEvent(sample);
        return new BaseResponse(ResponseMessage.CREATED);
    }

    /*
     * запрещаем редактирование за 3 часа до начала
     * количество участников уже больше, чем лимит в запросе
     * проверить владельца
     * */
}
