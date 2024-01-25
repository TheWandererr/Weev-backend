package com.pivo.weev.backend.domain.persistance.jpa.model.auth;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "verification_requests")
@SequenceGenerator(sequenceName = "verification_requests_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
public class VerificationRequestJpa extends SequencedPersistable<Long> {

    @Column(nullable = false)
    private String code;
    @Column(unique = true)
    private String email;
    @Column
    private Instant retryAfter;
    @Column
    private Instant expiresAt;
    @Column
    private boolean completed;

    public VerificationRequestJpa(String code, String email, Instant retryAfter, Instant expiresAt) {
        this.code = code;
        this.email = email;
        this.retryAfter = retryAfter;
        this.expiresAt = expiresAt;
    }

    public boolean isRetryable() {
        return !Instant.now().isBefore(retryAfter);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        Class<?> oEffectiveClass = other instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : other.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy thisHibernateProxy
                ? thisHibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        VerificationRequestJpa that = (VerificationRequestJpa) other;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
