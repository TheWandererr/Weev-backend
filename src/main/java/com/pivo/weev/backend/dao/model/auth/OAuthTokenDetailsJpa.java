package com.pivo.weev.backend.dao.model.auth;

import com.pivo.weev.backend.dao.model.common.SequencedPersistable;
import com.pivo.weev.backend.dao.utils.Constants.Columns;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "oauth_details",
    uniqueConstraints = {@UniqueConstraint(columnNames = {Columns.TOKEN_DETAILS_USER_ID, Columns.TOKEN_DETAILS_DEVICE_ID})}
)
@SequenceGenerator(sequenceName = "oauth_details_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class OAuthTokenDetailsJpa extends SequencedPersistable<Long> {

  @Column(name = Columns.TOKEN_DETAILS_USER_ID, nullable = false)
  private Long userId;
  @Column(name = Columns.TOKEN_DETAILS_DEVICE_ID, nullable = false)
  private String deviceId;
  @Column(nullable = false)
  private String serial;
  @Column(nullable = false)
  private Instant expiresAt;

}
