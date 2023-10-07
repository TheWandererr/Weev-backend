package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static java.util.Objects.isNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "configs", schema = "config")
@SequenceGenerator(sequenceName = "config_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Config extends ModifiableJpa<Long> {

    @Column(unique = true)
    private String name;
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> map;

    public Map<String, Object> getMap() {
        if (isNull(map)) {
            map = new HashMap<>();
        }
        return map;
    }
}