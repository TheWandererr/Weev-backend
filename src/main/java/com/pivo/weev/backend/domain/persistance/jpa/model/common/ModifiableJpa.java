package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.CREATED_DATE;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.MODIFIED_DATE;
import static java.util.Objects.nonNull;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

@MappedSuperclass
@Getter
@Setter
public abstract class ModifiableJpa<PK extends Serializable> extends SequencedPersistable<PK> {

    @CreationTimestamp
    @Column(name = CREATED_DATE)
    private Instant createdDate;

    @UpdateTimestamp
    @Column(name = MODIFIED_DATE)
    private Instant modifiedDate;

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
        ModifiableJpa<?> that = (ModifiableJpa<?>) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
