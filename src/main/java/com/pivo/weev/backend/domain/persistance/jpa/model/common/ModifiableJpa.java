package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.CREATED_DATE;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.MODIFIED_DATE;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class ModifiableJpa<PK extends Serializable> extends SequencedPersistable<PK> {

    @CreationTimestamp
    @Column(name = CREATED_DATE)
    private Instant createdDate;

    @UpdateTimestamp
    @Column(name = MODIFIED_DATE)
    private Instant modifiedDate;
}
