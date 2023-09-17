package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@SequenceGenerator(sequenceName = "notification_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
public class NotificationJpa extends ModifiableJpa<Long> {

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "recipient_id")
    private UserJpa recipient;
    @Column
    private String title;
    @Column
    @Enumerated(STRING)
    private Type type;
    @Column(columnDefinition = "boolean default false")
    private Boolean viewed;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "event_id")
    private EventJpa event;
    @ManyToOne
    @JoinColumn(name = "declination_reason_id")
    private DeclinationReason declinationReason;

    public enum Type {
        COMMON,
        INFO,
        IMPORTANT,
        CRITICAL
    }
}
