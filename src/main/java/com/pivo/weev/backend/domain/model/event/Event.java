package com.pivo.weev.backend.domain.model.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {

  private String header;
  private String category;
  private String subcategory;
  private Location location;
  private int membersLimit;
  private String description;
  private byte[] photoSource;
  private EntryFee entryFee;
  private Restrictions restrictions;
  private LocalDateTime localStartDateTime;
  private String startTimeZoneId;
  private LocalDateTime localEndDateTime;
  private String endTimeZoneId;

}
