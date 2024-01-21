package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static java.util.Objects.nonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "cloud_resources")
@SequenceGenerator(sequenceName = "cloud_resource_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class CloudResourceJpa extends SequencedPersistable<Long> {

    @Column(unique = true, nullable = false)
    private String blobId;
    @Column(unique = true, nullable = false)
    @Lob
    private String url;
    private String format;
    @Column(nullable = false)
    private Long authorId;

    @Override
    public final boolean equals(Object o) {
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
        CloudResourceJpa that = (CloudResourceJpa) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
