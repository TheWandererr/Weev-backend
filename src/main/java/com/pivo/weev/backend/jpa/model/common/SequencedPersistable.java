package com.pivo.weev.backend.jpa.model.common;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;


@MappedSuperclass
public class SequencedPersistable<PK extends Serializable> implements Entity {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "sequence_generator")
  @Column(nullable = false, unique = true)
  protected PK id;

  public SequencedPersistable() {
  }

  public SequencedPersistable(PK id) {
    this.id = id;
  }

  public PK getId() {
    return id;
  }

  public void setId(PK id) {
    this.id = id;
  }
}
