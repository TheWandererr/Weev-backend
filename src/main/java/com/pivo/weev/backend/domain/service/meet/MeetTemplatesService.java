package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.CREATED_DATE;
import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.mapping.jpa.MeetTemplateJpaMapper;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetTemplateRepositoryWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetTemplatesService {

    private final MeetTemplateRepositoryWrapper meetTemplateRepository;

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
}
