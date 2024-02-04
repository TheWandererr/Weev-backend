package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IMeetTemplateRepository extends IGenericRepository<Long, MeetTemplateJpa> {

    Page<MeetTemplateJpa> findAllByCreator_Id(Long id, Pageable pageable);

    @Modifying
    @Query(value = "delete from meet_templates mt where mt.creator_id = ?1", nativeQuery = true)
    void deleteAllByCreatorId(Long id);
}
