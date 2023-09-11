package com.pivo.weev.backend.jpa.model.event;

import com.pivo.weev.backend.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locations")
@SequenceGenerator(sequenceName = "location_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class LocationJpa extends SequencedPersistable<Long> {

  @Column
  private String country;
  @Column
  private String state;
  @Column
  private String city;
  @Column
  private String street;
  @Column
  private String road;
  @Column
  private String block;
  @Column
  private String building;
  @Column
  private String flat;
  @Column(unique = true)
  private Double lng;
  @Column(unique = true)
  private Double ltd;
}
