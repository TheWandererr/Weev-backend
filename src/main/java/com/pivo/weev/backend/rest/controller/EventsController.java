package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.rest.model.event.SearchContextRest.published;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.EventMapPoint;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.service.event.EventCrudService;
import com.pivo.weev.backend.domain.service.event.EventsSearchService;
import com.pivo.weev.backend.rest.mapping.domain.CreatableEventMapper;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventPreviewRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventReviewRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MapPointRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventMapPointRest;
import com.pivo.weev.backend.rest.model.event.EventPreviewRest;
import com.pivo.weev.backend.rest.model.event.EventReviewRest;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.EventSearchResponse;
import com.pivo.weev.backend.rest.model.response.EventsMapPointsSearchResponse;
import com.pivo.weev.backend.rest.model.response.EventsSearchResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/api/events")
@RequiredArgsConstructor
@Validated
public class EventsController {

    private final EventCrudService eventCrudService;
    private final EventsSearchService eventsSearchService;

    @PostMapping("/search")
    public EventsSearchResponse search(@Valid @RequestBody EventsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<Event> eventsPage = eventsSearchService.search(searchParams);
        List<EventPreviewRest> restEvents = getMapper(EventPreviewRestMapper.class).map(eventsPage.getContent());
        PageRest<EventPreviewRest> restEventsPage = new PageRest<>(restEvents, eventsPage.getNumber());
        return new EventsSearchResponse(restEventsPage, eventsPage.getTotalElements(), eventsPage.getTotalPages());
    }

    @PostMapping("/map/search")
    public EventsMapPointsSearchResponse searchMapPoints(@Valid @RequestBody EventsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<EventMapPoint> pointsPage = eventsSearchService.searchMapPoints(searchParams);
        List<EventMapPointRest> restPoints = mapToList(pointsPage.getContent(), eventMapPoint -> (EventMapPointRest) getMapper(MapPointRestMapper.class).map(eventMapPoint));
        PageRest<EventMapPointRest> restPointsPage = new PageRest<>(restPoints, pointsPage.getNumber());
        return new EventsMapPointsSearchResponse(restPointsPage, pointsPage.getTotalElements(), pointsPage.getTotalPages());
    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public BaseResponse createEvent(@Valid @ModelAttribute EventSaveRequest request) {
        CreatableEvent sample = getMapper(CreatableEventMapper.class).map(request);
        sample.setUpdatePhoto(true);
        eventCrudService.saveEvent(sample);
        return new BaseResponse(ResponseMessage.CREATED);
    }

    @GetMapping("/{id}")
    public EventSearchResponse search(@Min(1) @PathVariable Long id) {
        Event event = eventsSearchService.search(id);
        EventReviewRest restEvent = getMapper(EventReviewRestMapper.class).map(event);
        return new EventSearchResponse(restEvent);
    }

    @PutMapping
    public BaseResponse updateEvent(@Valid @ModelAttribute EventSaveRequest request) {
        CreatableEvent sample = getMapper(CreatableEventMapper.class).map(request);
        eventCrudService.updateEvent(sample);
        return new BaseResponse(ResponseMessage.SUCCESS);
    }

}
