package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class SequencedPersistable<PK extends Serializable> implements Entity {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "sequence_generator")
    @Column(nullable = false, unique = true)
    protected PK id;
}
