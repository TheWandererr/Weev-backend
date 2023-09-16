package com.pivo.weev.backend.jpa.model.event;

import com.pivo.weev.backend.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event_restrictions")
@SequenceGenerator(sequenceName = "event_restrictions_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class RestrictionsJpa extends SequencedPersistable<Long> {

    private Boolean joinAfterStartDisallowed;
    private Boolean joinByRequest;
}
