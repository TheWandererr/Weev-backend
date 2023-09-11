package com.pivo.weev.backend.dao.model.event;

import com.pivo.weev.backend.dao.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@SequenceGenerator(sequenceName = "event_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class EventJpa extends SequencedPersistable<Long> {

  @Column
  private String header;
  @OneToOne
  @JoinColumn(name = "category_id")
  private CategoryJpa category;
  @OneToOne
  @JoinColumn(name = "subcategory_id")
  private SubcategoryJpa subcategory;


}
