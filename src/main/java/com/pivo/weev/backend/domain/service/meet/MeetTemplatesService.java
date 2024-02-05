package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.CREATED_DATE;
import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.mapping.jpa.MeetTemplateJpaMapper;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetTemplateRepository;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetTemplatesService {

    private final UserResourceService userResourceService;

    private final MeetTemplateRepository meetTemplateRepository;
    private final MeetRepository meetRepository;

    @Transactional
    public Page<Meet> getMeetsTemplates(Long userId, Integer page, Integer pageSize) {
        Pageable pageable = build(page, pageSize, new String[]{CREATED_DATE});
        Page<MeetTemplateJpa> jpaPage = meetTemplateRepository.findAllByUserId(userId, pageable);
        List<Meet> content = getMapper(MeetMapper.class).mapTemplates(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }

    public void saveAsTemplate(MeetJpa meet) {
        MeetTemplateJpa meetTemplate = getMapper(MeetTemplateJpaMapper.class).map(meet);
        meetTemplateRepository.save(meetTemplate);
    }

    @Transactional
    public void createTemplate(Long meetId) {
        MeetJpa meet = meetRepository.fetch(meetId);
        if (meet.hasPrivateAvailability()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, FORBIDDEN);
        }
        MeetJpa copy = createAuthorsCopy(meet);
        saveAsTemplate(copy);
    }

    private MeetJpa createAuthorsCopy(MeetJpa meet) {
        UserJpa user = userResourceService.fetchUserJpa(getUserId());
        MeetJpa meetCopy = SerializationUtils.clone(meet);
        meetCopy.setCreator(user);
        return meetCopy;
    }

    @Transactional
    public void deleteTemplate(Long creatorId, Long templateId) {
        MeetTemplateJpa template = meetTemplateRepository.fetch(templateId);
        if (!Objects.equals(template.getCreator().getId(), creatorId)) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, FORBIDDEN);
        }
        meetTemplateRepository.forceDeleteById(templateId);
    }

    @Transactional
    public void deleteAllTemplates(Long creatorId) {
        meetTemplateRepository.forceDeleteAllByCreatorId(creatorId);
    }
}
