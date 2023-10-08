package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static jakarta.persistence.AccessType.FIELD;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import jakarta.persistence.Access;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

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
    @Column
    @Basic
    @Access(FIELD)
    private Point point;
}
