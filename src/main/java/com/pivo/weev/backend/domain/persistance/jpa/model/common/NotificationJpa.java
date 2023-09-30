package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@SequenceGenerator(sequenceName = "notification_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type", discriminatorType = DiscriminatorType.STRING)
public class NotificationJpa extends ModifiableJpa<Long> {

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "recipient_id")
    private UserJpa recipient;
    @Column
    private String title;
    @Column
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(columnDefinition = "boolean default false")
    private Boolean viewed;

    public enum Type {
        COMMON,
        INFO,
        IMPORTANT,
        CRITICAL
    }
}
