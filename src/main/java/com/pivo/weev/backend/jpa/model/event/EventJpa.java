package com.pivo.weev.backend.jpa.model.event;

import static com.pivo.weev.backend.jpa.utils.Constants.Columns.EVENT_HEADER;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.jpa.model.common.ModerationStatus;
import com.pivo.weev.backend.jpa.model.common.ModifiableJpa;
import com.pivo.weev.backend.jpa.model.user.UserJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@SequenceGenerator(sequenceName = "event_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class EventJpa extends ModifiableJpa<Long> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private UserJpa creator;
  @Column(nullable = false, name = EVENT_HEADER)
  private String header;
  @ManyToOne(optional = false)
  @JoinColumn(name = "category_id")
  private CategoryJpa category;
  @ManyToOne(optional = false)
  @JoinColumn(name = "subcategory_id")
  private SubcategoryJpa subcategory;
  @ManyToOne(fetch = LAZY, cascade = ALL, optional = false)
  @JoinColumn(name = "location_id")
  private LocationJpa location;
  @Embedded
  private EntryFeeJpa entryFee;
  private Integer membersLimit;
  @Column(columnDefinition = "TEXT")
  private String description;
  @OneToOne(cascade = ALL, orphanRemoval = true)
  @JoinColumn(name = "photo_cloud_resource_id")
  private CloudResourceJpa photo;
  private Boolean reminded;
  private Long moderatedBy;
  @OneToOne(fetch = LAZY, cascade = ALL, optional = false)
  private RestrictionsJpa restrictions;
  private LocalDateTime localStartDateTime;
  private String startTimeZoneId;
  private Instant utcStartDateTime;
  private LocalDateTime localEndDateTime;
  private String endTimeZoneId;
  private Instant utcEndDateTime;
  @Column
  @Enumerated(STRING)
  private ModerationStatus moderationStatus;

  public boolean hasRestrictions() {
    return nonNull(restrictions);
  }

}
