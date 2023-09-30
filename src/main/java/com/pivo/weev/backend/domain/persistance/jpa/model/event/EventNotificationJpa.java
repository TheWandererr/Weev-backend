package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("event")
public class EventNotificationJpa extends NotificationJpa {

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "event_id")
    private EventJpa event;
    @ManyToOne
    @JoinColumn(name = "declination_reason_id")
    private DeclinationReason declinationReason;
}
