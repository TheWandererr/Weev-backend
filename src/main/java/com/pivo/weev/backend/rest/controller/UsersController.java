package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.canceled;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.declined;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.onModeration;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.published;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MEET_REQUESTS_PER_PAGE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.model.meet.SearchParams.PageCriteria;
import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.user.UsersService;
import com.pivo.weev.backend.rest.annotation.ResourceOwner;
import com.pivo.weev.backend.rest.mapping.domain.DeviceMapper;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.DeviceSettingsRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetCompactedRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetJoinRequestRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import com.pivo.weev.backend.rest.model.meet.MeetJoinRequestRest;
import com.pivo.weev.backend.rest.model.request.DeviceSettingUpdateRequest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.DeviceSettingResponse;
import com.pivo.weev.backend.rest.model.response.MeetJoinRequestsResponse;
import com.pivo.weev.backend.rest.model.response.MeetsSearchResponse;
import com.pivo.weev.backend.rest.model.response.NicknameAvailabilityResponse;
import com.pivo.weev.backend.rest.model.user.DeviceSettingsRest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public MeetsSearchResponse searchConfirmedMeets(@PathVariable Long id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), published(id));
        return performSearch(searchParams);
    }

    private MeetsSearchResponse performSearch(SearchParams searchParams) {
        Page<Meet> meetsPage = meetSearchService.search(searchParams);
        List<MeetCompactedRest> restMeets = getMapper(MeetCompactedRestMapper.class).map(meetsPage.getContent());
        PageRest<MeetCompactedRest> pageRest = new PageRest<>(restMeets, meetsPage.getNumber());
        return new MeetsSearchResponse(pageRest, meetsPage.getTotalElements(), meetsPage.getTotalPages());
    }

    @ResourceOwner
    @GetMapping("/{id}/meets/on-moderation/{page}")
    public MeetsSearchResponse searchOnModerationMeets(@PathVariable Long id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), onModeration(id));
        return performSearch(searchParams);
    }

    @ResourceOwner
    @GetMapping("/{id}/meets/canceled/{page}")
    public MeetsSearchResponse searchCanceledMeets(@PathVariable Long id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), canceled(id));
        return performSearch(searchParams);
    }

    @ResourceOwner
    @GetMapping("/{id}/meets/declined/{page}")
    public MeetsSearchResponse searchDeclinedMeets(@PathVariable Long id, @PathVariable @Min(0) Integer page) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(new MeetsSearchRequest(page), declined(id));
        return performSearch(searchParams);
    }

    @ResourceOwner
    @PutMapping("/{userId}/devices/{deviceId}/settings")
    public DeviceSettingResponse updateDeviceSettings(@PathVariable Long userId, @PathVariable String deviceId, @RequestBody @Valid DeviceSettingUpdateRequest request) {
        Device device = getMapper(DeviceMapper.class).map(request, deviceId, userId);
        Settings settings = usersService.updateDeviceSettings(device);
        DeviceSettingsRest settingsRest = getMapper(DeviceSettingsRestMapper.class).map(settings);
        return new DeviceSettingResponse(settingsRest);
    }

    @ResourceOwner
    @GetMapping("/{userId}/meets/{meetId}/joining/requests/{page}")
    public MeetJoinRequestsResponse getMeetJoinRequests(@PathVariable Long userId, @PathVariable Long meetId, @PathVariable @Min(0) Integer page) {
        Page<MeetJoinRequest> joinRequests = usersService.getMeetJoinRequests(meetId, new PageCriteria(page, MEET_REQUESTS_PER_PAGE));
        List<MeetJoinRequestRest> restJoinRequests = getMapper(MeetJoinRequestRestMapper.class).map(joinRequests.getContent());
        PageRest<MeetJoinRequestRest> pageRest = new PageRest<>(restJoinRequests, joinRequests.getNumber());
        return new MeetJoinRequestsResponse(pageRest, joinRequests.getTotalElements(), joinRequests.getTotalPages());
    }
}
