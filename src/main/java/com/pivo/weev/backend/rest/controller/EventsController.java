package com.pivo.weev.backend.rest.controller;

import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import com.pivo.weev.backend.rest.model.response.EventCreatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/events")
@RequiredArgsConstructor
public class EventsController {

  @PostMapping
  public EventCreatedResponse createEvent(@Valid @RequestBody EventSaveRequest request) {
    return null;
  }
}
