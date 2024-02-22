package com.pivo.weev.backend.domain.persistance.jpa.model.meet;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "meet_requests",
       indexes = {@Index(columnList = "user_id, meet_id"), @Index(columnList = "meet_id")})
@SequenceGenerator(sequenceName = "meet_requests_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
public class MeetRequestJpa extends SequencedPersistable<Long> {

    @ManyToOne(fetch = EAGER, cascade = {MERGE, PERSIST}, optional = false)
    @JoinColumn(name = "meet_id")
    @Fetch(FetchMode.JOIN)
    private MeetJpa meet;
    @Column
    private Instant expiresAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public boolean equals(Object other) {
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
        MeetRequestJpa that = (MeetRequestJpa) other;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
