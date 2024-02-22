package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.model.meet.SearchContextRest.published;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ID_FORMAT_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.CREATED;

import com.pivo.weev.backend.domain.model.common.MapPointCluster;
import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.service.meet.MeetOperationsService;
import com.pivo.weev.backend.domain.service.meet.MeetRequestsService;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.meet.MeetTemplatesService;
import com.pivo.weev.backend.rest.mapping.domain.CreatableMeetMapper;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.mapping.rest.MapPointClusterRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetCompactedRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.MeetDetailedRestMapper;
import com.pivo.weev.backend.rest.model.common.MapPointClusterRest;
import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import com.pivo.weev.backend.rest.model.meet.MeetDetailedRest;
import com.pivo.weev.backend.rest.model.request.MeetSaveRequest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.MeetJoinResponse;
import com.pivo.weev.backend.rest.model.response.MeetSearchResponse;
import com.pivo.weev.backend.rest.model.response.MeetsMapPointClusterSearchResponse;
import com.pivo.weev.backend.rest.model.response.MeetsSearchResponse;
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
@RequestMapping("/api/meets")
@RequiredArgsConstructor
@Validated
public class MeetsController {

    private final MeetOperationsService meetOperationsService;
    private final MeetSearchService meetSearchService;
    private final MeetTemplatesService meetTemplatesService;
    private final MeetRequestsService meetRequestsService;

    @PostMapping("/search")
    public MeetsSearchResponse search(@Valid @RequestBody MeetsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<Meet> meetsPage = meetSearchService.search(searchParams);
        List<MeetCompactedRest> restMeets = getMapper(MeetCompactedRestMapper.class).map(meetsPage.getContent());
        PageRest<MeetCompactedRest> restMeetsPage = new PageRest<>(restMeets, meetsPage.getNumber());
        return new MeetsSearchResponse(restMeetsPage, meetsPage.getTotalElements(), meetsPage.getTotalPages());
    }

    @PostMapping("/map/search")
    public MeetsMapPointClusterSearchResponse searchMapPointClusters(@Valid @RequestBody MeetsSearchRequest searchRequest) {
        SearchParams searchParams = getMapper(SearchParamsMapper.class).map(searchRequest, published());
        Page<MapPointCluster> clustersPage = meetSearchService.searchMapPointClusters(searchParams);
        List<MapPointClusterRest> restClusters = mapToList(clustersPage.getContent(), pointCluster -> getMapper(MapPointClusterRestMapper.class).map(pointCluster));
        PageRest<MapPointClusterRest> restClustersPage = new PageRest<>(restClusters, clustersPage.getNumber());
        return new MeetsMapPointClusterSearchResponse(restClustersPage, clustersPage.getTotalElements(), clustersPage.getTotalPages());
    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public BaseResponse create(@Valid @ModelAttribute MeetSaveRequest request) {
        CreatableMeet sample = getMapper(CreatableMeetMapper.class).map(request);
        meetOperationsService.create(sample);
        return new BaseResponse(ResponseMessage.CREATED);
    }

    @GetMapping("/{id}")
    public MeetSearchResponse search(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        Meet meet = meetSearchService.search(id);
        MeetDetailedRest restMeet = getMapper(MeetDetailedRestMapper.class).map(meet);
        return new MeetSearchResponse(restMeet);
    }

    @PutMapping("/{id}")
    public BaseResponse update(@Valid @ModelAttribute MeetSaveRequest request) {
        CreatableMeet sample = getMapper(CreatableMeetMapper.class).map(request);
        meetOperationsService.update(sample);
        return new BaseResponse();
    }

    @PutMapping("/{id}/cancellation")
    public BaseResponse cancel(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        meetOperationsService.cancel(id);
        return new BaseResponse();
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        meetOperationsService.delete(id);
        return new BaseResponse();
    }

    @PutMapping("/{id}/joining")
    public MeetJoinResponse join(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        meetOperationsService.join(id);
        return new MeetJoinResponse(true);
    }

    @PutMapping("/{id}/leaving")
    public BaseResponse leave(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        meetOperationsService.leave(id);
        return new BaseResponse();
    }

    @PostMapping("/{id}/joining/requests")
    @ResponseStatus(value = CREATED)
    public MeetJoinResponse createJoinRequest(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long id) {
        meetRequestsService.createJoinRequest(id);
        return new MeetJoinResponse(false);
    }

    @PostMapping("/{meetId}/joining/requests/{requestId}/confirmation")
    @ResponseStatus(value = CREATED)
    public BaseResponse confirmJoinRequest(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long meetId,
                                           @Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long requestId) {
        meetRequestsService.confirmJoinRequest(requestId);
        return new BaseResponse();
    }

    @PostMapping("/{meetId}/joining/requests/{requestId}/declination")
    @ResponseStatus(value = CREATED)
    public BaseResponse declineJoinRequest(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long meetId,
                                           @Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long requestId) {
        meetRequestsService.declineJoinRequest(requestId);
        return new BaseResponse();
    }

    @PostMapping("/{meetId}/template")
    public BaseResponse createTemplate(@Min(value = 1, message = ID_FORMAT_ERROR) @PathVariable Long meetId) {
        meetTemplatesService.createTemplate(meetId);
        return new BaseResponse();
    }

}
