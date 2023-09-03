package com.pivo.weev.backend.dao.model;

import com.pivo.weev.backend.dao.model.common.SequencedPersistable;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@SequenceGenerator(sequenceName = "authority_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class AuthorityJpa extends SequencedPersistable<Long> {

  private String value;
}
