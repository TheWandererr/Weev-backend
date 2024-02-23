package com.pivo.weev.backend.domain.persistance.jpa.model.meet;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.MEET_HEADER;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.MEET_STATUS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.MEET_UTC_END_DATE_TIME;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.MEET_UTC_START_DATE_TIME;
import static com.pivo.weev.backend.utils.Constants.Amount.INFINITY;
import static com.pivo.weev.backend.utils.Constants.MeetAvailabilities.PRIVATE;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static java.time.Instant.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ModifiableJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "meets", indexes = @Index(columnList = "creator_id", unique = true))
@SequenceGenerator(sequenceName = "meet_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
@NoArgsConstructor
public class MeetJpa extends ModifiableJpa<Long> {

    @OneToOne(cascade = ALL, fetch = LAZY)
    @JoinColumn(name = "updatable_meet_id")
    private MeetJpa updatableTarget;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "creator_id")
    private UserJpa creator;
    @Column(nullable = false, name = MEET_HEADER)
    private String header;
    @ManyToOne(optional = false, fetch = EAGER)
    @JoinColumn(name = "category_id")
    private CategoryJpa category;
    @ManyToOne(optional = false, fetch = EAGER)
    @JoinColumn(name = "subcategory_id")
    private SubcategoryJpa subcategory;
    @ManyToOne(fetch = LAZY, cascade = {PERSIST, MERGE}, optional = false)
    @JoinColumn(name = "location_id")
    private LocationJpa location;
    private Integer membersLimit;
    @Lob
    private String description;
    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "photo_id")
    private CloudResourceJpa photo;
    private Boolean reminded = false;
    private Long moderatedBy;
    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = EAGER)
    @JoinColumn(name = "restrictions_id")
    private RestrictionsJpa restrictions = new RestrictionsJpa();
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    @Column(name = MEET_UTC_START_DATE_TIME)
    private Instant utcStartDateTime;
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
    @Column(name = MEET_UTC_END_DATE_TIME)
    private Instant utcEndDateTime;
    @Column(name = MEET_STATUS)
    @Enumerated(STRING)
    private MeetStatus status;
    @ManyToMany(mappedBy = "participatedMeets", fetch = LAZY, cascade = {PERSIST, MERGE})
    private Set<UserJpa> members = new HashSet<>();
    @OneToMany(fetch = LAZY, mappedBy = "meet")
    private Set<MeetJoinRequestJpa> requests = new HashSet<>();

    public MeetJpa(UserJpa creator, CategoryJpa category, SubcategoryJpa subcategory, LocationJpa location) {
        this.creator = creator;
        // this.creator.getCreatedMeets().add(this);
        this.category = category;
        this.subcategory = subcategory;
        this.location = location;
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

    public boolean isPublished() {
        return isConfirmed() || isHasModerationInstance();
    }

    public boolean isConfirmed() {
        return status == MeetStatus.CONFIRMED;
    }

    public boolean isHasModerationInstance() {
        return status == MeetStatus.HAS_MODERATION_INSTANCE;
    }

    public boolean isOnModeration() {
        return status == MeetStatus.ON_MODERATION;
    }

    public boolean isCanceled() {
        return status == MeetStatus.CANCELED;
    }

    public boolean isDeclined() {
        return status == MeetStatus.DECLINED;
    }

    public boolean isDeleted() {
        return status == MeetStatus.DELETED;
    }

    public boolean hasUpdatableTarget() {
        return nonNull(updatableTarget);
    }

    public boolean hasPhoto() {
        return nonNull(photo);
    }

    public boolean isEnded() {
        return now().isAfter(utcEndDateTime);
    }

    public boolean isStarted() {
        return !isEnded() && now().isAfter(utcStartDateTime);
    }

    public boolean hasMembersLimit() {
        return nonNull(membersLimit) && !INFINITY.equals(membersLimit);
    }

    public Set<UserJpa> getMembersWithCreator() {
        Set<UserJpa> users = new HashSet<>(this.members);
        users.add(creator);
        return users;
    }

    public boolean hasCreator(Long id) {
        return Objects.equals(creator.getId(), id);
    }

    public boolean hasCreator(String nickname) {
        return StringUtils.equals(creator.getNickname(), nickname);
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
        MeetJpa meetJpa = (MeetJpa) other;
        return nonNull(id) && Objects.equals(id, meetJpa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public void logicalDeleted() {
        setPhoto(null);
        getMembers().clear();
        setStatus(MeetStatus.DELETED);
    }

    public boolean hasPrivateAvailability() {
        return hasRestrictions() && PRIVATE.equals(getRestrictions().getAvailability());
    }
}
