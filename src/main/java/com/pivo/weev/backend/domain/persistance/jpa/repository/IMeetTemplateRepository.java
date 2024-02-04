package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMeetTemplateRepository extends IGenericRepository<Long, MeetTemplateJpa> {

    Page<MeetTemplateJpa> findAllByCreator_Id(Long id, Pageable pageable);
}
