package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static jakarta.persistence.AccessType.FIELD;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Access;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "locations")
@SequenceGenerator(sequenceName = "location_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class LocationJpa extends SequencedPersistable<Long> {

    @Column
    private String country;
    @Column
    private String state;
    @Column
    private String city;
    @Column
    private String street;
    @Column
    private String road;
    @Column
    private String block;
    @Column
    private String building;
    @Column
    private String flat;
    @Column(nullable = false)
    @Basic
    @Access(FIELD)
    private Point point;
    @Length(max = 12)
    @Column(nullable = false, length = 12)
    private String hash;

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
        LocationJpa that = (LocationJpa) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
