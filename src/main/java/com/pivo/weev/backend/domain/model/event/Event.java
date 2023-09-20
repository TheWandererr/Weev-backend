package com.pivo.weev.backend.domain.model.event;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.model.common.Identifiable;
import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.domain.model.user.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Event extends Identifiable {

    private User creator;
    private String header;
    private String category;
    private String subcategory;
    private Location location;
    private int membersLimit;
    private String description;
    private Image photo;
    private EntryFee entryFee;
    private Restrictions restrictions;
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    private Instant utcStartDateTime;
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
    private Instant utcEndDateTime;
    private Set<User> members;
    private String status;

    public Set<User> getMembers() {
        if (isNull(members)) {
            members = new HashSet<>();
        }
        return members;
    }

    public boolean hasPhoto() {
        return nonNull(photo);
    }
}
