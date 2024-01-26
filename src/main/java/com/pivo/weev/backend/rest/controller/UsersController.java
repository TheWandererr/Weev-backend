package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.rest.model.event.SearchContextRest.canceled;
import static com.pivo.weev.backend.rest.model.event.SearchContextRest.declined;
import static com.pivo.weev.backend.rest.model.event.SearchContextRest.onModeration;
import static com.pivo.weev.backend.rest.model.event.SearchContextRest.published;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.service.event.EventsSearchService;
import com.pivo.weev.backend.domain.service.user.UsersService;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.EventCompactedRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventCompactedRest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.EventsSearchResponse;
import com.pivo.weev.backend.rest.model.response.NicknameAvailabilityResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final EventsSearchService eventsSearchService;

    @GetMapping("/nickname/availability")
    public BaseResponse isNicknameAvailable(@RequestParam @NotBlank(message = MUST_BE_NOT_BLANK_ERROR) String nickname) {
        boolean nicknameAvailable = usersService.isNicknameAvailable(nickname.toLowerCase());
        return new NicknameAvailabilityResponse(nicknameAvailable);
    }

    @GetMapping("/{id}/events/confirmed/{page}")
    public EventsSearchResponse searchConfirmedEvents(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new EventsSearchRequest(page), published(getUserId()));
        return performSearch(searchParams);
    }

    private EventsSearchResponse performSearch(SearchParams searchParams) {
        Page<Event> eventsPage = eventsSearchService.search(searchParams);
        List<EventCompactedRest> restEvents = getMapper(EventCompactedRestMapper.class).mapCompacted(eventsPage.getContent());
        PageRest<EventCompactedRest> pageRest = new PageRest<>(restEvents, eventsPage.getNumber());
        return new EventsSearchResponse(pageRest, eventsPage.getTotalElements(), eventsPage.getTotalPages());
    }

    @GetMapping("/{id}/events/on-moderation/{page}")
    public EventsSearchResponse searchOnModerationEvents(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new EventsSearchRequest(page), onModeration(getUserId()));
        return performSearch(searchParams);
    }

    @GetMapping("/{id}/events/canceled/{page}")
    public EventsSearchResponse searchCanceledEvents(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new EventsSearchRequest(page), canceled(getUserId()));
        return performSearch(searchParams);
    }

    @GetMapping("/{id}/events/declined/{page}")
    public EventsSearchResponse searchDeclinedEvents(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new EventsSearchRequest(page), declined(getUserId()));
        return performSearch(searchParams);
    }
}
