package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.rest.model.event.SearchContextRest.published;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.model.common.MapPointCluster;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.service.event.EventRequestsService;
import com.pivo.weev.backend.domain.service.event.EventsOperationsService;
import com.pivo.weev.backend.domain.service.event.EventsSearchService;
import com.pivo.weev.backend.rest.mapping.domain.CreatableEventMapper;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventCompactedRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventDetailedRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MapPointClusterRestMapper;
import com.pivo.weev.backend.rest.model.common.MapPointClusterRest;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventCompactedRest;
import com.pivo.weev.backend.rest.model.event.EventDetailedRest;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.EventJoinResponse;
import com.pivo.weev.backend.rest.model.response.EventSearchResponse;
import com.pivo.weev.backend.rest.model.response.EventsMapPointClusterSearchResponse;
import com.pivo.weev.backend.rest.model.response.EventsSearchResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
public class EventsController {

    private final EventsOperationsService eventsOperationsService;
    private final EventsSearchService eventsSearchService;
    private final EventRequestsService eventRequestsService;

    @PostMapping("/search")
    public EventsSearchResponse search(@Valid @RequestBody EventsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<Event> eventsPage = eventsSearchService.search(searchParams);
        List<EventCompactedRest> restEvents = getMapper(EventCompactedRestMapper.class).mapCompacted(eventsPage.getContent());
        PageRest<EventCompactedRest> restEventsPage = new PageRest<>(restEvents, eventsPage.getNumber());
        return new EventsSearchResponse(restEventsPage, eventsPage.getTotalElements(), eventsPage.getTotalPages());
    }

    @PostMapping("/map/search")
    public EventsMapPointClusterSearchResponse searchMapPointClusters(@Valid @RequestBody EventsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<MapPointCluster> clustersPage = eventsSearchService.searchMapPointClusters(searchParams);
        List<MapPointClusterRest> restClusters = mapToList(clustersPage.getContent(), pointCluster -> getMapper(MapPointClusterRestMapper.class).map(pointCluster));
        PageRest<MapPointClusterRest> restClustersPage = new PageRest<>(restClusters, clustersPage.getNumber());
        return new EventsMapPointClusterSearchResponse(restClustersPage, clustersPage.getTotalElements(), clustersPage.getTotalPages());
    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public BaseResponse create(@Valid @ModelAttribute EventSaveRequest request) {
        CreatableEvent sample = getMapper(CreatableEventMapper.class).map(request);
        sample.setUpdatePhoto(true);
        eventsOperationsService.create(sample);
        return new BaseResponse(ResponseMessage.CREATED);
    }

    @GetMapping("/{id}")
    public EventSearchResponse search(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        Event event = eventsSearchService.search(id);
        EventDetailedRest restEvent = getMapper(EventDetailedRestMapper.class).map(event);
        return new EventSearchResponse(restEvent);
    }

    @PutMapping("/{id}")
    public BaseResponse update(@Valid @ModelAttribute EventSaveRequest request) {
        CreatableEvent sample = getMapper(CreatableEventMapper.class).map(request);
        eventsOperationsService.update(sample);
        return new BaseResponse();
    }

    @PutMapping("/{id}/cancellation")
    public BaseResponse cancel(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        eventsOperationsService.cancel(id);
        return new BaseResponse();
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        eventsOperationsService.delete(id);
        return new BaseResponse();
    }

    @PutMapping("/{id}/joining")
    public EventJoinResponse join(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        eventsOperationsService.join(id, getUserId());
        return new EventJoinResponse(true);
    }

    @PostMapping("/{id}/joining/requests")
    @ResponseStatus(value = CREATED)
    public EventJoinResponse createJoinRequest(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        eventRequestsService.createJoinRequest(id);
        return new EventJoinResponse(false);
    }

}
