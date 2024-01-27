package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.event.SearchContextRest.onModeration;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.service.event.EventsSearchService;
import com.pivo.weev.backend.domain.service.moderation.ModerationService;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventCompactedRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventCompactedRest;
import com.pivo.weev.backend.rest.model.request.EventDeclineRequest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.DeclinationReasonsResponse;
import com.pivo.weev.backend.rest.model.response.EventsSearchResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyAuthority('SCOPE_moderation')")
public class ModerationController {

    private final ModerationService moderationService;
    private final EventsSearchService eventsSearchService;

    @GetMapping("/declination-reasons")
    public DeclinationReasonsResponse getDeclinationReasons() {
        List<String> reasons = moderationService.getDeclinationReasons();
        return new DeclinationReasonsResponse(reasons);
    }

    @PutMapping("/events/{id}/confirmation")
    public BaseResponse confirmEvent(@PathVariable Long id) {
        moderationService.confirmEvent(id);
        return new BaseResponse();
    }

    @PutMapping("/events/{id}/declination")
    public BaseResponse declineEvent(@PathVariable Long id, @Valid @RequestBody EventDeclineRequest request) {
        moderationService.declineEvent(id, request.getDeclinationReason());
        return new BaseResponse();
    }

    @GetMapping("/events/{page}")
    public EventsSearchResponse searchEvents(@PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new EventsSearchRequest(page), onModeration());
        Page<Event> eventsPage = eventsSearchService.search(searchParams);
        List<EventCompactedRest> restEvents = getMapper(EventCompactedRestMapper.class).mapCompacted(eventsPage.getContent());
        PageRest<EventCompactedRest> pageRest = new PageRest<>(restEvents, eventsPage.getNumber());
        return new EventsSearchResponse(pageRest, eventsPage.getTotalElements(), eventsPage.getTotalPages());
    }
}
