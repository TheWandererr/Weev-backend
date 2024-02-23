package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface INotificationRepository extends IGenericRepository<Long, NotificationJpa> {

    @Query(value = "select n from NotificationJpa n where n.recipient.id = ?1")
    Page<NotificationJpa> findAllByRecipientId(Long id, Pageable pageable);

    @Query(value = "select count(n) from notifications n where n.recipient_id = ?1 and n.read = false", nativeQuery = true)
    Integer countAllUnreadByRecipientId(Long id);

    @Modifying
    @Query(value = "update NotificationJpa n set n.read = true where n.recipient.id = ?1 and n.createdAt between ?2 and ?3")
    void setReadByRecipientIdAndPeriod(Long userId, Instant from, Instant to);
}
