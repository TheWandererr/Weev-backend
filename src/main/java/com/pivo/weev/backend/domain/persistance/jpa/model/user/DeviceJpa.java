package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.DEVICE_INTERNAL_ID;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import com.pivo.weev.backend.domain.persistance.utils.Constants.Columns;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "devices",
       uniqueConstraints = {@UniqueConstraint(columnNames = {Columns.DEVICE_USER_ID, Columns.DEVICE_INTERNAL_ID})})
@SequenceGenerator(sequenceName = "device_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
public class DeviceJpa extends SequencedPersistable<Long> {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private UserJpa user;
    @Column(name = DEVICE_INTERNAL_ID, nullable = false)
    private String internalId;
    @OneToOne(cascade = ALL, orphanRemoval = true, optional = false, fetch = LAZY)
    @JoinColumn(name = "settings_id")
    private DeviceSettingsJpa settings = new DeviceSettingsJpa();
    private transient boolean created;

    public DeviceJpa(UserJpa user, String internalId, String lang) {
        this.user = user;
        this.internalId = internalId;
        this.settings = new DeviceSettingsJpa(lang);
        this.created = true;
    }

    public boolean hasPushNotificationToken() {
        return isNotBlank(getPushNotificationToken());
    }

    public String getPushNotificationToken() {
        return getSettings().getPushNotificationToken();
    }

    public String getLang() {
        return getSettings().getLang();
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
