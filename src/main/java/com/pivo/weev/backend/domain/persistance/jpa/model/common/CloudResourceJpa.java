package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cloud_resources")
@SequenceGenerator(sequenceName = "cloud_resource_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CloudResourceJpa extends SequencedPersistable<Long> {

    @Column(unique = true, nullable = false)
    private String externalId;
    @Column(unique = true, nullable = false)
    private String url;
    private Integer height;
    private Integer width;
    private String format;
    @Column(nullable = false)
    private Long authorId;
    private String signature;
}
