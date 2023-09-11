package com.pivo.weev.backend.dao.model.event;

import com.pivo.weev.backend.dao.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event_subcategories")
@SequenceGenerator(sequenceName = "event_subcategories_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class SubcategoryJpa extends SequencedPersistable<Long> {

  @Column
  private String name;
}
