package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "configs", schema = "config")
@SequenceGenerator(sequenceName = "config_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class Config extends ModifiableJpa<Long> {

    @Column(unique = true)
    private String name;
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> map;
    @Column
    private Integer integer;

    public Map<String, Object> getMap() {
        if (isNull(map)) {
            map = new HashMap<>();
        }
        return map;
    }

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
        Config that = (Config) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}