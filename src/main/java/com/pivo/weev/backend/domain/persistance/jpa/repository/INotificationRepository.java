package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface INotificationRepository extends IGenericRepository<Long, NotificationJpa> {

    @Query(value = "select n.* from notifcations n where n.recipient_id = ?1", nativeQuery = true)
    Page<NotificationJpa> findAllByRecipientId(Long id, Pageable pageable);

    @Query(value = "select count(n) from notifications n where n.recipient_id = ?1 and n.read = false", nativeQuery = true)
    Integer countAllUnreadByRecipientId(Long id);
}
