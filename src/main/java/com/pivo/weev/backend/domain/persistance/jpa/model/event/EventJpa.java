package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DECLINED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DELETED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_HEADER;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_STATUS;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_UTC_END_DATE_TIME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_UTC_START_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.domain.utils.Constants.MessageCodes.EVENT_CAPACITY_EXCEEDED;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ModifiableJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "events")
@SequenceGenerator(sequenceName = "event_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
public class EventJpa extends ModifiableJpa<Long> {

    @OneToOne(cascade = ALL, fetch = LAZY)
    @JoinColumn(name = "updatable_event_id")
    private EventJpa updatableTarget;
    @ManyToOne(fetch = LAZY)
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
    @ManyToOne(fetch = LAZY, cascade = {PERSIST, MERGE}, optional = false)
    @JoinColumn(name = "location_id")
    private LocationJpa location;
    @Embedded
    private EntryFeeJpa entryFee;
    private Integer membersLimit;
    @Column(columnDefinition = "TEXT")
    private String description;
    @OneToOne(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    private CloudResourceJpa photo;
    private Boolean reminded;
    private Long moderatedBy;
    @Embedded
    private RestrictionsJpa restrictions;
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    @Column(name = EVENT_UTC_START_DATE_TIME)
    private Instant utcStartDateTime;
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
    @Column(name = EVENT_UTC_END_DATE_TIME)
    private Instant utcEndDateTime;
    @Column(name = EVENT_STATUS)
    @Enumerated(STRING)
    private EventStatus status;
    @ManyToMany(mappedBy = "participatedEvents", fetch = LAZY, cascade = ALL)
    private Set<UserJpa> members;

    public EventJpa(UserJpa creator) {
        this.creator = creator;
        creator.getCreatedEvents().add(this);
    }

    public Set<UserJpa> getMembers() {
        if (isNull(members)) {
            members = new HashSet<>();
        }
        return members;
    }

    public boolean hasRestrictions() {
        return nonNull(restrictions);
    }

    public boolean isOnModeration() {
        return status == ON_MODERATION;
    }

    public boolean isCanceled() {
        return status == CANCELED;
    }

    public boolean isDeclined() {
        return status == DECLINED;
    }

    public boolean isDeleted() {
        return status == DELETED;
    }

    public boolean hasUpdatableTarget() {
        return nonNull(updatableTarget);
    }

    public boolean hasPhoto() {
        return nonNull(photo);
    }

    public Set<UserJpa> dissolve() {
        Set<UserJpa> membersCopy = new HashSet<>(this.members);
        for (UserJpa member : this.members) {
            member.getParticipatedEvents().remove(this);
        }
        this.members.clear();
        return membersCopy;
    }

    public Set<UserJpa> getUsers() {
        Set<UserJpa> users = new HashSet<>(this.members);
        users.add(creator);
        return users;
    }

    public void addMember(UserJpa user) {
        if (isNull(user)) {
            return;
        }
        if (nonNull(membersLimit) && membersLimit > 0 && getMembers().size() == membersLimit) {
            throw new ReasonableException(OPERATION_IMPOSSIBLE_ERROR, EVENT_CAPACITY_EXCEEDED);
        }
        getMembers().add(user);
        user.getParticipatedEvents().add(this);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        Class<?> oEffectiveClass = other instanceof HibernateProxy oHibernateProxy
                ? oHibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : other.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        EventJpa eventJpa = (EventJpa) other;
        return nonNull(id) && Objects.equals(id, eventJpa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
