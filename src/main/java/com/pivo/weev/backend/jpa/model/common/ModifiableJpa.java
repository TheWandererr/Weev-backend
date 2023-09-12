package com.pivo.weev.backend.jpa.model.common;

import static com.pivo.weev.backend.jpa.utils.Constants.Columns.CREATED_DATE;
import static com.pivo.weev.backend.jpa.utils.Constants.Columns.MODIFIED_DATE;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
public class ModifiableJpa<PK extends Serializable> extends SequencedPersistable<PK> {

  @CreationTimestamp
  @Column(name = CREATED_DATE)
  private Instant createdDate;

  @UpdateTimestamp
  @Column(name = MODIFIED_DATE)
  private Instant modifiedDate;
}
