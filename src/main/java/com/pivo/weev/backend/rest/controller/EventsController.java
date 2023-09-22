package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.event.SearchContextRest.published;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.service.event.EventsOperatingService;
import com.pivo.weev.backend.domain.service.event.EventsSearchService;
import com.pivo.weev.backend.rest.mapping.domain.CreatableEventMapper;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventPreviewRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventPreviewRest;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.EventsSearchResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/events")
@RequiredArgsConstructor
@Validated
public class EventsController {

    private final EventsOperatingService eventsOperatingService;
    private final EventsSearchService eventsSearchService;

    @PostMapping("/search")
    public EventsSearchResponse search(@Valid @RequestBody EventsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<Event> eventsPage = eventsSearchService.search(searchParams);
        List<EventPreviewRest> restEvents = getMapper(EventPreviewRestMapper.class).map(eventsPage.getContent());
        PageRest<EventPreviewRest> restEventsPage = new PageRest<>(restEvents, eventsPage.getNumber());
        return new EventsSearchResponse(restEventsPage, eventsPage.getTotalElements(), eventsPage.getTotalPages());
    }

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
