package com.pivo.weev.backend.rest.controller;

import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.service.EventsService;
import com.pivo.weev.backend.rest.mapping.EventMapper;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/events")
@RequiredArgsConstructor
public class EventsController {

  private final EventsService eventsService;

  @PostMapping
  @ResponseStatus(value = CREATED)
  public BaseResponse createEvent(@Valid @RequestBody EventSaveRequest request) {
    Event sample = getMapper(EventMapper.class).map(request);
    eventsService.saveEvent(sample);
    return new BaseResponse(ResponseMessage.CREATED);
  }
}
