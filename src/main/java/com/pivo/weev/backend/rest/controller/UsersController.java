package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.canceled;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.declined;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.onModeration;
import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.published;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.CHATS_PER_PAGE;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MEET_REQUESTS_PER_PAGE;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MEET_TEMPLATES_PER_PAGE;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MESSAGES_PER_PAGE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.model.meet.SearchParams.PageCriteria;
import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.service.meet.MeetRequestsService;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.meet.MeetTemplatesService;
import com.pivo.weev.backend.domain.service.messaging.ChatService;
import com.pivo.weev.backend.domain.service.user.DeviceService;
import com.pivo.weev.backend.domain.service.user.ProfileService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.rest.annotation.ResourceOwner;
import com.pivo.weev.backend.rest.mapping.domain.DeviceMapper;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.domain.UserMapper;
import com.pivo.weev.backend.rest.mapping.rest.ChatRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.DeviceSettingsRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.ImageRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetCompactedRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetJoinRequestRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetTemplateRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.ProfileRestMapper;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.ImageRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import com.pivo.weev.backend.rest.model.meet.MeetJoinRequestRest;
import com.pivo.weev.backend.rest.model.meet.MeetTemplateRest;
import com.pivo.weev.backend.rest.model.messaging.ChatRest;
import com.pivo.weev.backend.rest.model.request.DeviceSettingUpdateRequest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import com.pivo.weev.backend.rest.model.request.ProfileUpdateRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.ChatsResponse;
import com.pivo.weev.backend.rest.model.response.DeviceSettingResponse;
import com.pivo.weev.backend.rest.model.response.ImageResponse;
import com.pivo.weev.backend.rest.model.response.MeetJoinRequestsResponse;
import com.pivo.weev.backend.rest.model.response.MeetTemplatesResponse;
import com.pivo.weev.backend.rest.model.response.MeetsSearchResponse;
import com.pivo.weev.backend.rest.model.response.NicknameAvailabilityResponse;
import com.pivo.weev.backend.rest.model.response.ProfileResponse;
import com.pivo.weev.backend.rest.model.user.DeviceSettingsRest;
import com.pivo.weev.backend.rest.model.user.ProfileRest;
import com.pivo.weev.backend.rest.validation.annotation.ValidImage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UserResourceService userResourceService;
    private final ProfileService profileService;
    private final MeetSearchService meetSearchService;
    private final MeetRequestsService meetRequestsService;
    private final MeetTemplatesService meetTemplatesService;
    private final DeviceService deviceService;
    private final ChatService chatService;

    @GetMapping("/nickname/availability")
    public BaseResponse isNicknameAvailable(@RequestParam @NotBlank(message = MUST_BE_NOT_BLANK_ERROR) String nickname) {
        boolean nicknameAvailable = userResourceService.isNicknameAvailable(nickname.toLowerCase());
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
        Settings settings = deviceService.updateDeviceSettings(device);
        DeviceSettingsRest settingsRest = getMapper(DeviceSettingsRestMapper.class).map(settings);
        return new DeviceSettingResponse(settingsRest);
    }

    @ResourceOwner
    @GetMapping("/{userId}/meets/{meetId}/joining/requests/{page}")
    public MeetJoinRequestsResponse getMeetJoinRequests(@PathVariable Long userId, @PathVariable Long meetId, @PathVariable @Min(0) Integer page) {
        Page<MeetJoinRequest> joinRequests = meetRequestsService.getMeetJoinRequests(meetId, new PageCriteria(page, MEET_REQUESTS_PER_PAGE));
        List<MeetJoinRequestRest> restJoinRequests = getMapper(MeetJoinRequestRestMapper.class).map(joinRequests.getContent());
        PageRest<MeetJoinRequestRest> pageRest = new PageRest<>(restJoinRequests, joinRequests.getNumber());
        return new MeetJoinRequestsResponse(pageRest, joinRequests.getTotalElements(), joinRequests.getTotalPages());
    }

    @GetMapping("/{id}")
    public ProfileResponse getProfile(@PathVariable Long id) {
        User user = userResourceService.fetchUser(id);
        ProfileRest profile = getMapper(ProfileRestMapper.class).map(user);
        return new ProfileResponse(profile);
    }

    @ResourceOwner
    @PutMapping("/{id}")
    public ProfileResponse updateProfile(@PathVariable Long id, @RequestBody @Valid ProfileUpdateRequest request) {
        User sample = getMapper(UserMapper.class).map(request);
        sample.setId(id);
        User updatedUser = profileService.updateProfile(sample);
        ProfileRest profile = getMapper(ProfileRestMapper.class).map(updatedUser);
        return new ProfileResponse(profile);
    }

    @ResourceOwner
    @PutMapping("/{id}/photo")
    public ImageResponse updatePhoto(@PathVariable Long id, @ModelAttribute @ValidImage MultipartFile photo) {
        Image image = profileService.updatePhoto(id, photo);
        ImageRest restImage = getMapper(ImageRestMapper.class).map(image);
        return new ImageResponse(restImage);
    }

    @ResourceOwner
    @GetMapping("/{id}/meets/templates/{page}")
    public MeetTemplatesResponse getTemplates(@PathVariable Long id, @PathVariable @Min(0) Integer page) {
        Page<Meet> templatesPage = meetTemplatesService.getMeetsTemplates(id, page, MEET_TEMPLATES_PER_PAGE);
        List<MeetTemplateRest> restTemplates = getMapper(MeetTemplateRestMapper.class).map(templatesPage.getContent());
        PageRest<MeetTemplateRest> pageRest = new PageRest<>(restTemplates, templatesPage.getNumber());
        return new MeetTemplatesResponse(pageRest, templatesPage.getTotalElements(), templatesPage.getTotalPages());
    }

    @ResourceOwner
    @DeleteMapping("/{id}/meets/templates/{templateId}")
    public BaseResponse deleteTemplate(@PathVariable Long id, @PathVariable Long templateId) {
        meetTemplatesService.deleteTemplate(id, templateId);
        return new BaseResponse();
    }

    @ResourceOwner
    @DeleteMapping("/{id}/meets/templates")
    public BaseResponse deleteTemplate(@PathVariable Long id) {
        meetTemplatesService.deleteAllTemplates(id);
        return new BaseResponse();
    }

    @ResourceOwner
    @GetMapping("/{id}/chats")
    public ChatsResponse getChats(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id,
                                  @RequestParam(required = false, defaultValue = CHATS_PER_PAGE) @Min(1) Integer limit,
                                  @RequestParam(required = false, defaultValue = "0") @Min(0) Integer offset,
                                  @RequestParam(required = false, defaultValue = MESSAGES_PER_PAGE) @Min(1) Integer historySize) {
        List<Chat> chats = chatService.getChats(id, offset, limit, historySize);
        List<ChatRest> restChats = getMapper(ChatRestMapper.class).map(chats);
        return new ChatsResponse(restChats);
    }
}
