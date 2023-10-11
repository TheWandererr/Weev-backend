package com.pivo.weev.backend.domain.persistance.jpa.model.auth;

import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(
        name = "oauth_details",
        uniqueConstraints = {@UniqueConstraint(columnNames = {Columns.TOKEN_DETAILS_USER_ID, Columns.TOKEN_DETAILS_DEVICE_ID})}
)
@SequenceGenerator(sequenceName = "oauth_details_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class OAuthTokenDetailsJpa extends SequencedPersistable<Long> {

    @Column(name = Columns.TOKEN_DETAILS_USER_ID, nullable = false)
    private Long userId;
    @Column(name = Columns.TOKEN_DETAILS_DEVICE_ID, nullable = false)
    private String deviceId;
    @Column(nullable = false)
    private String serial;
    @Column(nullable = false)
    private Instant expiresAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy oHibernateProxy
                ? oHibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        OAuthTokenDetailsJpa that = (OAuthTokenDetailsJpa) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
