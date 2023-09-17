package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@SequenceGenerator(sequenceName = "authority_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AuthorityJpa extends SequencedPersistable<Long> {

    private String value;
}
