package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.canceled;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.declined;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.onModeration;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.published;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.user.UsersService;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetCompactedRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.MeetsSearchResponse;
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
    private final MeetSearchService meetSearchService;

    @GetMapping("/nickname/availability")
    public BaseResponse isNicknameAvailable(@RequestParam @NotBlank(message = MUST_BE_NOT_BLANK_ERROR) String nickname) {
        boolean nicknameAvailable = usersService.isNicknameAvailable(nickname.toLowerCase());
        return new NicknameAvailabilityResponse(nicknameAvailable);
    }

    @GetMapping("/{id}/meets/confirmed/{page}")
    public MeetsSearchResponse searchConfirmedMeets(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), published(getUserId()));
        return performSearch(searchParams);
    }

    private MeetsSearchResponse performSearch(SearchParams searchParams) {
        Page<Meet> meetsPage = meetSearchService.search(searchParams);
        List<MeetCompactedRest> restMeets = getMapper(MeetCompactedRestMapper.class).mapCompacted(meetsPage.getContent());
        PageRest<MeetCompactedRest> pageRest = new PageRest<>(restMeets, meetsPage.getNumber());
        return new MeetsSearchResponse(pageRest, meetsPage.getTotalElements(), meetsPage.getTotalPages());
    }

    @GetMapping("/{id}/meets/on-moderation/{page}")
    public MeetsSearchResponse searchOnModerationMeets(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), onModeration(getUserId()));
        return performSearch(searchParams);
    }

    @GetMapping("/{id}/meets/canceled/{page}")
    public MeetsSearchResponse searchCanceledMeets(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), canceled(getUserId()));
        return performSearch(searchParams);
    }

    @GetMapping("/{id}/meets/declined/{page}")
    public MeetsSearchResponse searchDeclinedMeets(@PathVariable String id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), declined(getUserId()));
        return performSearch(searchParams);
    }
}
