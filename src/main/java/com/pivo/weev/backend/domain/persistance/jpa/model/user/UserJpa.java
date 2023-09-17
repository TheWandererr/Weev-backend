package com.pivo.weev.backend.domain.persistance.jpa.model.user;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_EMAIL;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_NAME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_PASSWORD;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.USER_PHONE_NUMBER;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ModifiableJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Check(constraints = "(email is not null or phone_number is not null) and nickname <> email")
@SequenceGenerator(sequenceName = "client_id_sequence", allocationSize = 1, name = "sequence_generator")
@Getter
@Setter
public class UserJpa extends ModifiableJpa<Long> {

    @NotBlank
    @Length(max = 120)
    @Column(length = 120, name = USER_NAME, nullable = false)
    private String name;
    @NotBlank
    @Length(max = 20)
    @Column(length = 20, name = USER_NICKNAME, unique = true, nullable = false)
    private String nickname;
    @NotBlank
    @Column(name = USER_PASSWORD, nullable = false)
    private String password;
    @Length(max = 80)
    @Column(length = 80, name = USER_EMAIL, unique = true)
    private String email;
    @Length(max = 20)
    @Column(length = 20, name = USER_PHONE_NUMBER, unique = true)
    private String phoneNumber;
    @OneToOne(cascade = ALL)
    @JoinColumn(name = "role_id")
    private UserRoleJpa role;
    private Boolean active;
    @GenericGenerator(name = "event_member_id_generator", type = SequenceStyleGenerator.class)
    @JoinTable(
            name = "event_members",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "event_id"})},
            indexes = {
                    @Index(name = "event_members_event_id_index", columnList = "event_id"),
                    @Index(name = "event_members_user_id_index", columnList = "user_id"),
            }
    )
    @ManyToMany(fetch = LAZY, cascade = ALL)
    private Set<EventJpa> participatedEvents;
}
