package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLanguage;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "device_settings")
@SequenceGenerator(sequenceName = "device_settings_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class DeviceSettingsJpa extends SequencedPersistable<Long> {

    @Column
    private String pushNotificationToken;
    @Column(nullable = false)
    private String lang;

    public DeviceSettingsJpa() {
        setLang(getAcceptedLanguage());
    }

    public DeviceSettingsJpa(String lang) {
        this.lang = lang;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        Class<?> oEffectiveClass = other instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : other.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        DeviceSettingsJpa that = (DeviceSettingsJpa) other;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
