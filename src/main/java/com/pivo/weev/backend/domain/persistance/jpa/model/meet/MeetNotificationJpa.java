package com.pivo.weev.backend.domain.persistance.jpa.model.meet;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Discriminators.MEET_NOTIFICATION;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@DiscriminatorValue(MEET_NOTIFICATION)
public class MeetNotificationJpa extends NotificationJpa {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "meet_id", updatable = false)
    private MeetJpa meet;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "declination_reason_id", updatable = false)
    private DeclinationReasonJpa declinationReasonJpa;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(updatable = false)
    private Map<String, Object> details = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy oProxy
                ? oProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        MeetNotificationJpa that = (MeetNotificationJpa) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
