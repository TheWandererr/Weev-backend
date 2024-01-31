package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.onModeration;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.moderation.ModerationService;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetCompactedRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import com.pivo.weev.backend.rest.model.request.MeetDeclineRequest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.DeclinationReasonsResponse;
import com.pivo.weev.backend.rest.model.response.MeetsSearchResponse;
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
    private final MeetSearchService meetSearchService;

    @GetMapping("/declination-reasons")
    public DeclinationReasonsResponse getDeclinationReasons() {
        List<String> reasons = moderationService.getDeclinationReasons();
        return new DeclinationReasonsResponse(reasons);
    }

    @PutMapping("/meets/{id}/confirmation")
    public BaseResponse confirmMeet(@PathVariable Long id) {
        moderationService.confirmMeet(id);
        return new BaseResponse();
    }

    @PutMapping("/meets/{id}/declination")
    public BaseResponse declineMeet(@PathVariable Long id, @Valid @RequestBody MeetDeclineRequest request) {
        moderationService.declineMeet(id, request.getDeclinationReason());
        return new BaseResponse();
    }

    @GetMapping("/meets/{page}")
    public MeetsSearchResponse searchMeets(@PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), onModeration());
        Page<Meet> meetsPage = meetSearchService.search(searchParams);
        List<MeetCompactedRest> restMeets = getMapper(MeetCompactedRestMapper.class).mapCompacted(meetsPage.getContent());
        PageRest<MeetCompactedRest> pageRest = new PageRest<>(restMeets, meetsPage.getNumber());
        return new MeetsSearchResponse(pageRest, meetsPage.getTotalElements(), meetsPage.getTotalPages());
    }
}
