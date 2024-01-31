package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static jakarta.persistence.FetchType.LAZY;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "devices")
@SequenceGenerator(sequenceName = "device_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
public class DeviceJpa extends SequencedPersistable<Long> {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private UserJpa user;
    private String internalId;
    private String notificationToken;

    public DeviceJpa(UserJpa user, String internalId) {
        this.internalId = internalId;
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        DeviceJpa deviceJpa = (DeviceJpa) o;
        return getId() != null && Objects.equals(getId(), deviceJpa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
