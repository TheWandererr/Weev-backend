package com.pivo.weev.backend.domain.model.event;

import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Event {

  private String header;
  private String category;
  private String subcategory;
  private Location location;
  private int membersLimit;
  private String description;
  private MultipartFile photo;
  private EntryFee entryFee;
  private Restrictions restrictions;
  private LocalDateTime localStartDateTime;
  private String startTimeZoneId;
  private LocalDateTime localEndDateTime;
  private String endTimeZoneId;

  public boolean hasPhoto() {
    return nonNull(photo);
  }

}
