package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.USER_EMAIL;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.USER_NAME;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.USER_NICKNAME;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.USER_PASSWORD;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Columns.USER_PHONE_NUMBER;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ModifiableJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Check(constraints = "(email is not null or phone_number is not null) and nickname <> email")
@SequenceGenerator(sequenceName = "client_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class UserJpa extends ModifiableJpa<Long> {

    @Length(max = 120)
    @Column(length = 120, name = USER_NAME)
    private String name;
    @Length(max = 20)
    @Column(length = 20, name = USER_NICKNAME, unique = true, nullable = false)
    private String nickname;
    @Column(name = USER_PASSWORD, nullable = false)
    private String password;
    @Length(max = 80)
    @Column(length = 80, name = USER_EMAIL, unique = true)
    private String email;
    @Length(max = 20)
    @Column(length = 20, name = USER_PHONE_NUMBER, unique = true)
    private String phoneNumber;
    @ManyToOne(cascade = ALL, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    @Fetch(FetchMode.JOIN)
    @BatchSize(size = 10)
    private UserRoleJpa role;
    private Boolean active = false;
    @GenericGenerator(name = "meet_member_id_generator", type = SequenceStyleGenerator.class)
    @JoinTable(
            name = "meet_members",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "meet_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "meet_id"})},
            indexes = {
                    @Index(name = "meet_members_meet_id_index", columnList = "meet_id"),
                    @Index(name = "meet_members_user_id_index", columnList = "user_id"),
            }
    )
    @ManyToMany(cascade = {MERGE, PERSIST}, fetch = LAZY)
    private Set<MeetJpa> participatedMeets = new HashSet<>();
    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "avatar_id")
    private CloudResourceJpa avatar;
    @OneToMany(fetch = LAZY, mappedBy = "creator", cascade = {PERSIST, MERGE})
    private Set<MeetJpa> createdMeets = new HashSet<>();
    @OneToMany(fetch = LAZY, mappedBy = "recipient", cascade = {PERSIST, MERGE})
    private Set<NotificationJpa> notifications = new HashSet<>();
    @OneToMany(fetch = LAZY, mappedBy = "user", cascade = {PERSIST, MERGE})
    private Set<DeviceJpa> devices = new HashSet<>();
    @Column
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy oProxy
                ? oProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        UserJpa that = (UserJpa) o;
        return nonNull(id) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }

    public boolean hasEmail() {
        return isNotBlank(email);
    }

    public boolean hasPhoneNumber() {
        return isNotBlank(phoneNumber);
    }

    @Override
    public void logicalDeleted() {
        getDevices().clear();
        getNotifications().clear();
        setActive(false);
        setDeleted(true);
    }

    private Set<MeetJpa> getCreatedMeets() {
        return createdMeets;
    }

    private Set<MeetJpa> getParticipatedMeets() {
        return participatedMeets;
    }
}
