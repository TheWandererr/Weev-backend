package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "event_requests", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "event_id"})})
@SequenceGenerator(sequenceName = "event_requests_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EventRequestJpa extends SequencedPersistable<Long> {

    @ManyToOne(fetch = LAZY, cascade = ALL, optional = false)
    @JoinColumn(name = "event_id")
    private EventJpa event;
    @OneToOne(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private UserJpa user;
    @Column
    private Instant expiresAt;

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        Class<?> oEffectiveClass = other instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : other.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy thisHibernateProxy ? thisHibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        EventRequestJpa that = (EventRequestJpa) other;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
