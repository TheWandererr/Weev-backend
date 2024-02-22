package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface INotificationRepository extends IGenericRepository<Long, NotificationJpa> {

    @Query("select n from NotificationJpa n where n.recipient.id = ?1")
    Page<NotificationJpa> findAllByRecipientId(Long id, Pageable pageable);
}
