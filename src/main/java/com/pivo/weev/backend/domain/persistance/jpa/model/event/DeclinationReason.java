package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "declination_reasons")
@SequenceGenerator(sequenceName = "declination_reason_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class DeclinationReason extends SequencedPersistable<Long> {

    @Column(unique = true)
    private String title;
}
